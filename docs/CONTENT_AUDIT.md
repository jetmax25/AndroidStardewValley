# Stardew reference audit

Access date: 6 November 2026.

This is a record of what was compared with the official English wiki, what changed in the app, and what was not checked. It is not a claim that every number in the app is now correct.

## Version this audit targets

The [Mobile Version History](https://stardewvalleywiki.com/Mobile_Version_History) page, read on 6 November 2026, says the current Android game is equivalent to PC 1.6.15, with multiplayer still experimental. Reference data was checked against that version of the wiki. Play Store listings the same day showed Android build 1.6.15.3 (26 June 2026) as the latest update.

## Sources

| Page | URL |
| --- | --- |
| Crops | https://stardewvalleywiki.com/Crops |
| Crop edibility and sell-price modules | https://stardewvalleywiki.com/mediawiki/index.php?title=Module:Calcedibility and Module:Calcsellprice |
| Fish, including night market and legendary II | https://stardewvalleywiki.com/Fish |
| Individual fish infoboxes | https://stardewvalleywiki.com/ (one page per fish, for edibility) |
| Artifacts | https://stardewvalleywiki.com/Artifacts |
| Minerals | https://stardewvalleywiki.com/Minerals |
| Lost Books | https://stardewvalleywiki.com/Lost_Books |
| Bundles (standard, not remixed) | https://stardewvalleywiki.com/Bundles |
| Villagers | https://stardewvalleywiki.com/Villagers |
| List of All Gifts, including birthdays | https://stardewvalleywiki.com/List_of_All_Gifts |
| Farm Maps | https://stardewvalleywiki.com/Farm_Maps |
| Item descriptions | Template:Description on the same wiki |

## Categories checked

| Category | What was compared | Result |
| --- | --- | --- |
| Crops | Every crop on the Crops page that uses the normal growth table: seasons, days per stage, total days, regrowth, seed price, sell price, edibility, gold/day | Corrected and extended. See gaps below. |
| Fish caught with a rod, night-market submarine, and both legendary families | Season, time, weather, difficulty, behavior, XP, base price, and edibility | Corrected and extended. Crab-pot catches were not added. |
| Artifacts | Names, sell prices, and the description template | 42 names already matched, aside from the strange-doll titles. A few descriptions and the Palm Fossil price changed. Dig locations were not rewritten. |
| Minerals | Names, sell prices, and the description template for the 53 donation minerals | Names already matched. A few descriptions changed. Geodes were not added. Mine floor ranges were not rechecked one by one. |
| Lost books | All 21 titles and bodies on the Lost Books page | Replaced the 18 damaged entries with the wiki text. |
| Community Center, standard bundles | Rewards, bundle names, item names that could not find a sprite, and season flags that were clearly wrong | Rewards and names corrected. Remixed bundles were not added. |
| Villagers | Giftable roster, marriage candidates, and birthdays | Roster and birthdays already matched. Heart-event paragraphs were not compared line by line. |
| Gifts | Duplicate rows, names that missed their sprites, explicit bullet tastes, and the "All Artifacts" / "All Fish" lines | Those parts were corrected. The rest of the taste matrix was not rebuilt from the universal rules. |
| Farm types | The eight maps on Farm Maps | Three missing maps were added. |
| Images | Every drawable name the JSON and farm types ask for | 790 names checked. 61 have no file and use the existing placeholder. |

## Image fixes

Sprites are chosen from the item or bundle name. Typos and missing apostrophes were asking for files that do not exist, so those rows showed the placeholder even though the art was already in the app.

- Cranberry seeds now use `item_cranberry_seeds`.
- Orpiment, Dwarvish Helm, Triple Shot Espresso, and Rainbow Trout use the sprites that were already named correctly.
- Blacksmith's, Geologist's, Adventurer's, Chef's, and Enchanter's bundles use the existing bundle icons.
- Rabbit's Foot, quality Wine, Ancient Fruit, and Void Salmon use the base item sprite.
- "More Wood" uses the wood sprite. Gold amounts such as `2,500g` still use the gold sprite.
- Crop stage and regrowth images that are not in the project use the placeholder instead of an invalid resource id. That avoids a crash on the crop detail screen.
- Legendary fish that do not have a map file show their location text. The original five legends still show their maps.

No new game art was added. The 61 names in `app/src/test/resources/known_placeholder_drawables.txt` are the ones that still fall back to `default_image`.

## Data corrections

### Crops

Most harvest times were stored as 7 days. They now use the total on the Crops page (Parsnip 4, Cauliflower 12, Starfruit 13, Ancient Fruit 28, and the rest of the table). Silver and gold energy and health now follow the wiki modules: energy is `floor(ceil(edibility * 2.5) + edibility * quality)`, health is `floor(energy * 0.45)`, and sell prices use the 1 / 1.25 / 1.5 quality steps.

Corn, Sunflower, and Wheat are summer and fall. Ancient Fruit seed price is 0 because the seed is crafted from the artifact; the listed gold per day already assumed a free seed. Cranberries name their seed packet.

Added, from the same page: Carrot, Unmilled Rice, Summer Squash, Broccoli, Powdermelon, Pineapple, and Taro Root. Pineapple and Taro Root are summer crops on the valley farm. The page also says they grow all year on Ginger Island. The crop screen only has the four valley seasons, so that island exception is not a separate flag.

Unmilled Rice gold per day is the unirrigated figure on the page (-1.25). Irrigated rice is faster and is not a second entry.

### Fish

Times, seasons, weather, difficulty, behavior, XP, prices, and energy were taken from the Fish page and each fish's infobox. Examples that were wrong: Bream is 6pm–2am, Anchovy is spring and fall rather than every season, and Pufferfish also lists Ginger Island. Where the page says a fish is also caught all year on Ginger Island, that is an extra location line. The season icons stay the valley seasons.

Added: Stingray, Lionfish, Blue Discus, Goby, Son of Crimsonfish, Ms. Angler, Legend II, Glacierfish Jr., and Radioactive Carp. The extended family uses the wiki's "any season, any weather" rule and the level printed in the location text (Legend II requires level 10, Son of Crimsonfish level 5). Goby's infobox edibility is -25, so the screen shows it as poisonous.

Overnight ranges that used to end at 2 (before the start hour) now end at 26. The label is still 2 AM, and the time filter can match them.

### Museum

Palm Fossil sell price is 100g, not 50g. Dwarf Scroll I, Dwarf Scroll IV, Ornamental Fan, Golden Relic, Frozen Tear, Diamond, Esperite, Fluorapatite, Pyrite, and Fairy Stone descriptions now match Template:Description.

Strange Doll 1 and Strange Doll 2 keep those names so the existing sprites still load. The wiki titles are Strange Doll (green) and Strange Doll (yellow).

Lost books are the 21 entries on the Lost Books page, including Tips on Farming, the Marnie book, and Note From Gunther. The two secret books stay titled `???` and `????`. A database migration rewrites saved museum and bundle ids when a title or bundle name changed, so an existing check is not cleared.

### Community Center

Standard bundle rewards that were out of date or misspelled:

- River Fish Bundle reward is 30 Deluxe Bait, not 30 Bait (1.6).
- Night Fishing Bundle reward is a Glow Ring, not a Small Glow Ring (1.6).
- Construction Bundle reward is a Charcoal Kiln.
- Blacksmith's Bundle reward is a Furnace.
- Exotic Foraging reward is 5 Autumn's Bounty.
- Specialty Fish reward is 5 Dish O' The Sea.
- Possessive bundle names match the icons.

A few guides were stored as lists. The loader now accepts a list, and the file stores a single string, so opening the Community Center does not fail on the first of those items. The Wild Horseradish guide had described winter artifact spots; it now describes spring foraging.

Season flags that were clearly contradicted by the wiki: Hardwood is all year, Truffle is spring through fall, Honey is spring through fall on the valley farm, Corn and Sunflower include summer, Catfish is not a winter river fish, Carp is also winter, and red and purple mushrooms are available all year from the mines.

### People and gifts

Birthdays on List of All Gifts already matched `villagers.json`. No birthday or marriage-flag edits were required.

Duplicate gift rows were removed. Renames: Oripment to Orpiment, Dwavish Helm to Dwarvish Helm, Triple Shot Expresso to Triple Shot Espresso, the stray space on Rainbow Trout, and Pina Colada to Piña Colada. Explicit loved/liked/neutral/disliked/hated bullets were applied, and so were the "All Artifacts" and "All Fish" lines (with the exceptions written on those lines). That covers cases such as Penny and the Dwarf liking artifacts, Maru loving a Dwarf Gadget, and Willy loving Catfish, Octopus, Sturgeon, and Gold Bar.

### Farms

Four Corners, Beach, and Meadowlands can be chosen with the other farm types. There is no bundled picture for them, so the placeholder is used. Older saves keep their current type.

## Behavior that changed on purpose

- Choosing a season in the crop or fish list includes items that are available in every season. They used to disappear whenever a single season was selected.
- A missing crop stage uses the placeholder image.
- Saved checklist ids for the renamed bundles, Rabbit's Foot, the Missing Bundle items, and the cleaned lost-book titles are rewritten in database version 4.

Favorites are not stored as a separate list. Progress is the per-farm sets of community center items, caught fish, and museum items. Fish names that already existed were not renamed. New fish start unchecked.

## Not checked, or only partly checked

- Heart-event text. Leo and Krobus still have no events stored. The other villagers' event paragraphs were not compared sentence by sentence.
- Gift tastes that come only from a universal category such as "all cooking" or "all vegetables", except the All Artifacts and All Fish lines. Most gift cells were left as they were.
- Crab-pot catches (lobster, crab, and the rest). They do not use the fishing minigame fields this screen is built around.
- Remixed bundles, and the Joja Community Development form. The screen is the standard Community Center list.
- Fiber, Tea Leaves, and Mixed Seeds. Fiber sells for 1g and the Crops page does not give it the same gold-per-day figure. Tea is harvested only in the last week of the season. Mixed seeds are not one crop.
- Artifact dig spots and mineral mine ranges, beyond the price and description pass.
- Strange-doll display names, for the sprite reason above.
- Fishing level when the wiki page does not print "Requires level N". Existing levels were left in place.
- Ginger Island's all-year seasons are a location note, not extra season icons.
- No device or emulator was available here, so the screens were not opened and there are no screenshots. Image coverage was checked by name against `res/drawable`.

## How to repeat the check

```bash
python3 tools/check_reference_data.py
./gradlew testDebugUnitTest
```

The Python check fails if a referenced image is missing and not listed as a known placeholder, if Corn/Sunflower/Wheat omit fall, if Blue Jazz's silver energy drifts from the wiki formula, if Bream or Anchovy regress, if a gift name is duplicated, if a bundle guide is not text, or if the River Fish reward is not Deluxe Bait.
