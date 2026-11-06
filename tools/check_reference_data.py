#!/usr/bin/env python3
"""Checks bundled Stardew reference data and drawable coverage.

Run from the repository root:
    python3 tools/check_reference_data.py
"""
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RAW = ROOT / "app/src/main/res/raw"
DRAWABLE = ROOT / "app/src/main/res/drawable"
ALLOWLIST = ROOT / "app/src/test/resources/known_placeholder_drawables.txt"

QUALITY_PARENS = re.compile(r"(?i)\s*\([^)]*quality[^)]*\)")
QUALITY_PREFIX = re.compile(r"(?i)^(silver or better quality|gold or iridium quality|gold quality|silver quality)\s+")
QUALITY_SUFFIX = re.compile(r"(?i)\s+(silver|gold|iridium) quality$")
MONEY = re.compile(r"\d+_\d+g|\d+g")
ALIASES = {
    "oripment": "orpiment",
    "dwavish helm": "dwarvish helm",
    "triple shot expresso": "triple shot espresso",
    "pina colada": "pina colada",
    "piña colada": "pina colada",
    "more wood": "wood",
    "cranberries seeds": "cranberry seeds",
    "rabbits foot": "rabbit's foot",
    "farmers lunch": "farmer's lunch",
}


def slug(value):
    return re.sub(r"\W+", "_", value.lower()).strip("_")


def item_drawable(name):
    cleaned = QUALITY_SUFFIX.sub("", QUALITY_PREFIX.sub("", QUALITY_PARENS.sub("", name.strip()))).strip()
    cleaned = ALIASES.get(cleaned.lower(), cleaned)
    slugged = slug(cleaned)
    if MONEY.fullmatch(slugged):
        return "item_gold"
    return "item_" + slugged


def load_allowlist():
    names = set()
    for line in ALLOWLIST.read_text().splitlines():
        line = line.split("#", 1)[0].strip()
        if line:
            names.add(line)
    return names


def main():
    errors = []
    drawables = {path.stem for path in DRAWABLE.iterdir() if path.is_file()}
    referenced = {}

    def expect(resource, label):
        referenced.setdefault(resource, label)

    crops = json.loads((RAW / "crops.json").read_text())
    if "Fall" not in [season for season, on in crops["Corn"]["Seasons"].items() if on]:
        errors.append("Corn must grow in Fall")
    if "Fall" not in [season for season, on in crops["Sunflower"]["Seasons"].items() if on]:
        errors.append("Sunflower must grow in Fall")
    if "Fall" not in [season for season, on in crops["Wheat"]["Seasons"].items() if on]:
        errors.append("Wheat must grow in Fall")
    if crops["Blue Jazz"]["Stats"]["Silver"]["Energy"] != 63:
        errors.append("Blue Jazz silver energy must use the wiki edibility formula (63)")
    if crops["Parsnip"]["Harvest Time"] not in (4, "4"):
        errors.append("Parsnip harvest time must be 4 days")
    for required in ("Carrot", "Unmilled Rice", "Summer Squash", "Broccoli", "Powdermelon", "Pineapple", "Taro Root"):
        if required not in crops:
            errors.append(f"Missing crop {required}")
    for name, crop in crops.items():
        expect(item_drawable(name), f"crop {name}")
        if crop.get("Seeds"):
            expect(item_drawable(crop["Seeds"]), f"seed {name}")
        else:
            expect(f"item_{slug(name)}_seeds", f"seed {name}")
        phases = 1 + sum(1 for stage in crop["Stages"] if stage)
        for phase in range(1, phases + 1):
            expect(f"item_{slug(name)}_phase_{phase}", f"phase {name} {phase}")
        if crop.get("Regrowth Days", 0) > 0:
            expect(f"item_{slug(name)}_regrowth", f"regrowth {name}")

    fishes = json.loads((RAW / "fishes.json").read_text())
    if fishes["Bream"]["Availability"]["Time"]["Start"] != 18:
        errors.append("Bream must start at 6pm")
    if fishes["Anchovy"]["Availability"]["Seasons"]["Winter"]:
        errors.append("Anchovy is not a winter fish")
    for required in ("Stingray", "Lionfish", "Blue Discus", "Goby", "Legend II"):
        if required not in fishes:
            errors.append(f"Missing fish {required}")
    for name in fishes:
        expect(item_drawable(name), f"fish {name}")

    gifts = json.loads((RAW / "gift_reactions.json").read_text())
    names = [gift["giftName"] for gift in gifts]
    if len(names) != len(set(names)):
        errors.append("Gift names must be unique")
    for gift in gifts:
        expect(item_drawable(gift["giftName"]), f"gift {gift['giftName']}")

    for filename in ("artifacts.json", "minerals.json"):
        for name in json.loads((RAW / filename).read_text()):
            expect(item_drawable(name), f"{filename} {name}")

    books = json.loads((RAW / "lost_books.json").read_text())
    if len(books) != 21:
        errors.append(f"Expected 21 lost books, found {len(books)}")

    bundles = json.loads((RAW / "community_center.json").read_text())
    river = None
    for room in bundles.values():
        for bundle_name, bundle in room["Bundles"].items():
            cleaned = slug(bundle_name.lower().replace("bundle", "").strip())
            expect(f"community_center_bundle_{cleaned}", f"bundle {bundle_name}")
            if bundle_name == "River Fish Bundle":
                river = bundle
            for item_name, item in bundle["Items"].items():
                if not isinstance(item["Guide"], str):
                    errors.append(f"Guide for {item_name} must be a string")
                expect(item_drawable(item_name), f"bundle item {item_name}")
    if river is None or river["Reward"]["Item"] != "Deluxe Bait":
        errors.append("River Fish Bundle reward must be Deluxe Bait")
    horseradish = bundles["Craft Room"]["Bundles"]["Spring Foraging Bundle"]["Items"]["Wild Horseradish"]["Guide"]
    if "Winter" in horseradish:
        errors.append("Wild Horseradish guide still describes winter")

    farm_source = (ROOT / "app/src/main/java/com/pickledgames/stardewvalleyguide/enums/FarmType.kt").read_text()
    for farm in ("FourCorners", "Beach", "Meadowlands"):
        if farm not in farm_source:
            errors.append(f"FarmType is missing {farm}")
        expect(f"farm_type_{slug(farm if farm != 'FourCorners' else 'Four Corners')}", farm)

    allowlist = load_allowlist()
    missing = sorted(resource for resource in referenced if resource not in drawables)
    unexpected = [resource for resource in missing if resource not in allowlist]
    stale = sorted(resource for resource in allowlist if resource in drawables)
    if unexpected:
        errors.append("Referenced images with no drawable and no placeholder entry:\n  " + "\n  ".join(unexpected))
    if stale:
        errors.append("Placeholder allowlist includes files that now exist:\n  " + "\n  ".join(stale))

    if errors:
        print("\n".join(errors))
        return 1
    print(f"Reference data ok. {len(referenced)} image names checked, {len(missing)} use the placeholder.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
