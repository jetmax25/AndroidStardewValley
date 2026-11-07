#!/usr/bin/env python3
"""List guide entries whose image does not resolve to a drawable.

Mirrors the resource names the app builds in:

- ImageUtil.getImageId (missing name -> R.drawable.default_image)
- AssetNames for crops, fish, gifts, museum items, bundle items, and bundles
- LostBook (misc_lost_book, shared by every book)
- Villager (villager_<name>, also used for gift-reaction portraits)
- MuseumItemCollection, FarmType, Season
- LegendaryFishingLocation map and location sprites for the original five
  legends. Later legends show location text and do not load a map sprite.

Names listed in app/src/test/resources/known_placeholder_drawables.txt are
the intentional default_image fallbacks. They are not missing. A name that
is neither a file nor on that list is missing.

Run from anywhere:

    python3 scripts/check_item_images.py

Exits 0 when nothing is missing, 1 otherwise.
"""

from __future__ import annotations

import json
import re
import sys
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DRAWABLE = ROOT / "app" / "src" / "main" / "res" / "drawable"
RAW = ROOT / "app" / "src" / "main" / "res" / "raw"
ALLOWLIST = ROOT / "app" / "src" / "test" / "resources" / "known_placeholder_drawables.txt"

RESOURCE_NAME = re.compile(r"^[a-z_][a-z0-9_]*$")


QUALITY_PARENS = re.compile(r"(?i)\s*\([^)]*quality[^)]*\)")
QUALITY_PREFIX = re.compile(
    r"(?i)^(silver or better quality|gold or iridium quality|gold quality|silver quality)\s+"
)
QUALITY_SUFFIX = re.compile(r"(?i)\s+(silver|gold|iridium) quality$")
MONEY = re.compile(r"\d+_\d+g|\d+g")
ITEM_ALIASES = {
    "oripment": "orpiment",
    "dwavish helm": "dwarvish helm",
    "triple shot expresso": "triple shot espresso",
    "pina colada": "pina colada",
    "piña colada": "pina colada",
    "more wood": "wood",
    "cranberries seeds": "cranberry seeds",
    "rabbits foot": "rabbit's foot",
    "farmers lunch": "farmer's lunch",
    "large egg": "large egg white",
    "large brown egg": "large egg brown",
}
BUNDLE_ALIASES = {
    "g2500": "2,500",
    "g5000": "5,000",
    "g10000": "10,000",
    "g25000": "25,000",
}


def load_allowlist() -> set[str]:
    names: set[str] = set()
    for line in ALLOWLIST.read_text().splitlines():
        line = line.split("#", 1)[0].strip()
        if line:
            names.add(line)
    return names


def slug(value: str) -> str:
    """Keep in sync with AssetNames.slug."""
    return re.sub(r"\W+", "_", value.lower()).strip("_")


def item_drawable(name: str) -> str:
    """Keep in sync with AssetNames.itemDrawable."""
    cleaned = QUALITY_SUFFIX.sub(
        "", QUALITY_PREFIX.sub("", QUALITY_PARENS.sub("", name.strip()))
    ).strip()
    cleaned = ITEM_ALIASES.get(cleaned.lower(), cleaned)
    slugged = slug(cleaned)
    if not slugged:
        return "default_image"
    if MONEY.fullmatch(slugged):
        return "item_gold"
    return f"item_{slugged}"


def bundle_resource(name: str) -> str:
    """Keep in sync with AssetNames.bundleDrawable."""
    cleaned = re.sub(r"^(Remix|Joja):\s*", "", name)
    cleaned = cleaned.lower().replace("bundle", "").strip().replace("'", "")
    cleaned = BUNDLE_ALIASES.get(cleaned, cleaned)
    return f"community_center_bundle_{slug(cleaned)}"


def check_normalizer() -> None:
    expected = {
        "Wine Silver Quality": "item_wine",
        "Ancient Fruit (Gold Quality)": "item_ancient_fruit",
        "Void Salmon (Gold or iridium Quality)": "item_void_salmon",
        "Silver or better quality Wine": "item_wine",
        "Gold quality Ancient Fruit": "item_ancient_fruit",
        "Parsnip High Quality": "item_parsnip_high_quality",
        "2,500g": "item_gold",
        "10,000g": "item_gold",
        "25,000g": "item_gold",
        "Gold Bar": "item_gold_bar",
        "Wild Horseradish": "item_wild_horseradish",
        "Quality Fertilizer": "item_quality_fertilizer",
        "Piña Colada": "item_pina_colada",
    }
    for name, resource in expected.items():
        actual = item_drawable(name)
        if actual != resource:
            raise SystemExit(f"normalizer mismatch for {name!r}: {actual} != {resource}")
    if bundle_resource("Blacksmith's Bundle") != "community_center_bundle_blacksmiths":
        raise SystemExit("bundle normalizer mismatch")
    if bundle_resource("Remix: Blacksmiths Bundle") != "community_center_bundle_blacksmiths":
        raise SystemExit("remix bundle normalizer mismatch")
    if bundle_resource("2,500 Bundle") != "community_center_bundle_2_500":
        raise SystemExit("vault bundle normalizer mismatch")
    if bundle_resource("The Missing Bundle") != "community_center_bundle_the_missing":
        raise SystemExit("missing bundle normalizer mismatch")


def main() -> int:
    check_normalizer()
    if not DRAWABLE.is_dir():
        print(f"drawable directory not found: {DRAWABLE}", file=sys.stderr)
        return 1

    assets = {path.stem for path in DRAWABLE.iterdir() if path.is_file()}
    missing: list[tuple[str, str, str, str]] = []
    checked: Counter[str] = Counter()

    def expect(kind: str, entry: str, resource: str, note: str) -> None:
        checked[kind] += 1
        if not RESOURCE_NAME.match(resource) or resource not in assets:
            missing.append((kind, entry, resource, note))

    crops = json.loads((RAW / "crops.json").read_text())
    for name, data in crops.items():
        expect("crop", name, item_drawable(name), "ImageUtil falls back to default_image")
        seeds = data.get("Seeds")
        if seeds:
            expect(
                "crop-seed",
                f"{name} / {seeds}",
                item_drawable(seeds),
                "getSeedImageId falls back to the crop icon",
            )
        else:
            seed_resource = f"item_{slug(name)}_seeds"
            if seed_resource not in assets:
                expect(
                    "crop-seed",
                    name,
                    seed_resource,
                    "getSeedImageId falls back to the crop icon",
                )
            else:
                checked["crop-seed"] += 1
        for index in range(len(data.get("Stages") or [])):
            expect(
                "crop-stage",
                f"{name} phase {index + 1}",
                f"item_{slug(name)}_phase_{index + 1}",
                "ImageUtil falls back to default_image",
            )
        if int(data.get("Regrowth Days") or 0) > 0:
            expect(
                "crop-regrowth",
                name,
                f"item_{slug(name)}_regrowth",
                "ImageUtil falls back to default_image",
            )

    for name in json.loads((RAW / "fishes.json").read_text()):
        expect("fish", name, item_drawable(name), "ImageUtil falls back to default_image")

    for filename, kind in (("minerals.json", "mineral"), ("artifacts.json", "artifact")):
        for name in json.loads((RAW / filename).read_text()):
            expect(kind, name, item_drawable(name), "ImageUtil falls back to default_image")

    for name in json.loads((RAW / "lost_books.json").read_text()):
        expect("lost-book", name, "misc_lost_book", "ImageUtil falls back to default_image")

    villagers = json.loads((RAW / "villagers.json").read_text())
    seen_villagers = set()
    for name in villagers:
        seen_villagers.add(name)
        # Villager lowercases the name and does not replace punctuation.
        expect(
            "villager",
            name,
            f"villager_{name.lower()}",
            "ImageUtil falls back to default_image",
        )

    gifts = json.loads((RAW / "gift_reactions.json").read_text())
    seen_gifts = set()
    for gift in gifts:
        name = gift["giftName"]
        if name not in seen_gifts:
            seen_gifts.add(name)
            expect("gift", name, item_drawable(name), "ImageUtil falls back to default_image")
        for villager_name in gift["reactions"]:
            if villager_name in seen_villagers:
                continue
            seen_villagers.add(villager_name)
            expect(
                "villager",
                villager_name,
                f"villager_{villager_name.lower()}",
                "gift reaction portrait falls back to default_image",
            )

    community_center = json.loads((RAW / "community_center.json").read_text())
    for room in community_center.values():
        for bundle_name, bundle in room.get("Bundles", {}).items():
            expect(
                "bundle",
                bundle_name,
                bundle_resource(bundle_name),
                "ImageUtil falls back to default_image",
            )
            for item_name in bundle.get("Items", {}):
                expect(
                    "cc-item",
                    f"{item_name} [{bundle_name}]",
                    item_drawable(item_name),
                    "ImageUtil falls back to default_image",
                )

    for farm in (
        "Standard",
        "Riverland",
        "Forest",
        "Hilltop",
        "Wilderness",
        "Four Corners",
        "Beach",
        "Meadowlands",
    ):
        expect("farm", farm, f"farm_type_{slug(farm)}", "ImageUtil falls back to default_image")
    for season in ("Fall", "Winter", "Spring", "Summer"):
        expect("season", season, f"season_{season.lower()}", "ImageUtil falls back to default_image")

    # FishLocationsAdapter only loads map sprites for LegendaryFishingLocation.
    for fish_name in ("Angler", "Crimsonfish", "Glacierfish", "Legend", "Mutant Carp"):
        map_slug = slug(fish_name)
        expect(
            "legendary-map",
            fish_name,
            f"misc_map_{map_slug}",
            "getMapImageId falls back to default_image",
        )
        expect(
            "legendary-location",
            fish_name,
            f"misc_location_{map_slug}",
            "getLocationImageId falls back to default_image",
        )

    for name, resource in (
        ("Minerals", "item_diamond"),
        ("Artifacts", "misc_artifact"),
        ("Lost Books", "misc_lost_book"),
    ):
        expect("museum-collection", name, resource, "hardcoded collection drawable")

    allowlist = load_allowlist()
    unexpected = [row for row in missing if row[2] not in allowlist]
    placeholders = [row for row in missing if row[2] in allowlist]
    stale = sorted(name for name in allowlist if name in assets)

    print(f"checked {sum(checked.values())} image references")
    print(f"placeholders {len(placeholders)}")
    if stale:
        print("placeholder list includes files that now exist:")
        for name in stale:
            print(name)
        return 1
    if unexpected:
        print(f"missing {len(unexpected)}")
        for kind, entry, resource, note in unexpected:
            print(f"{kind}\t{entry}\t{resource}\t{note}")
        return 1

    print("missing 0")
    return 0


if __name__ == "__main__":
    sys.exit(main())
