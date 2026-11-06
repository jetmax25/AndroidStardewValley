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

The same season rule now applies on the Community Center list. Year-round bundle items used to disappear when one season was selected.

## Second pass, 6 November 2026

Same wiki pages as above, plus:

| Page | URL |
| --- | --- |
| Wiki copyright | https://stardewvalleywiki.com/Stardew_Valley_Wiki:Copyrights |
| Official FAQ (video permission only) | https://www.stardewvalley.net/faq/ |
| Friendship universal dislikes and hates | https://stardewvalleywiki.com/Friendship |
| Artisan Goods | https://stardewvalleywiki.com/Artisan_Goods |
| Remixed Bundles | https://stardewvalleywiki.com/Remixed_Bundles |
| JojaMart community projects | https://stardewvalleywiki.com/JojaMart |
| Bouquet group 10-heart event | https://stardewvalleywiki.com/Bouquet |
| Each giftable villager page | https://stardewvalleywiki.com/Villagers |

### Images and licensing

The 61 names from the first pass still have no file in this project. The wiki text and its images are CC BY-NC-SA 3.0. That license forbids commercial use. The official FAQ allows monetized videos of the game and does not grant a license to ship game sprites in an app. This app has ads and a paid upgrade, so copying wiki or game sprites into it is not an allowed source. No public-domain or freely licensed replacement for those 61 sprites was found. They were not replaced with generated art.

Adding Fiber, Tea, Mixed Seeds, crab-pot fish, Joja projects, and remix-only bundles created more names that also have no licensed file. `python3 tools/check_reference_data.py` now checks 837 referenced names. **108 use the placeholder.** The list is `app/src/test/resources/known_placeholder_drawables.txt`. Remix bundles that match a standard bundle reuse that bundle's existing icon. `Large Egg` uses the existing white large-egg sprite, and `Large Brown Egg` uses the existing brown one. Joja gold amounts use the existing gold sprite.

### What this pass filled in

- Heart events: all 34 giftable villagers now have events, 135 in total, including Leo (2, 3, 4, 6, 7, 9) and Krobus (3 and 14). Triggers, choice labels, and friendship-point changes come from each villager page. Where the page uses the shared group 10-heart cutscene, the entry points at the Bouquet page: enter the saloon after bouquets, 10 hearts, and individual 10-heart events with every bachelor or bachelorette. A Rabbit's Foot makes it friendly. Without one, the group is upset.
- Universal gifts: likes, neutrals, dislikes, and hates from Friendship and List of All Gifts are applied, then each villager's "All ..." line, then that villager's named items. Checked examples: Penny loves Emerald, Alex dislikes Alamite, Willy loves Catfish, Sebastian likes Flounder.
- Crab pots: Clam, Cockle, Mussel, Oyster, Shrimp, Snail, Periwinkle, Crab, Lobster, and Crayfish. They use behavior "Crab Pot", 5 fishing XP, and are not eaten directly. Ocean versus freshwater (shown as river and lake) follows the Fish page. Deluxe Bait can make them silver.
- Fiber, Tea Leaves, Mixed Seeds, and Mixed Flower Seeds are on the crop list. Tea is harvested every day of the last week of Spring, Summer, and Fall, and Winter if it is indoors. Mixed seeds are not one crop. Their detail note says so, and the list shows 0 days because the screen has no random-crop field. Fiber's gold per day is 0 for the same reason: the page says 4-7 Fiber and does not give one gold-per-day number.
- Pineapple, Taro Root, and Unmilled Rice have a note on the crop screen. Pineapple and Taro are summer on the valley farm and all year on Ginger Island. Unirrigated rice is the 8-day timing; irrigated rice is 6 days.
- Artifact rows now use the dig and drop lines from the Artifacts table. Foraged minerals keep their floor ranges (Quartz 1-120, Earth Crystal 1-39, Frozen Tear 40-79, Fire Quartz 80-120). Source lines for gems and geode minerals were filled from that table.
- Remixed bundles are extra entries prefixed "Remix:". They do not change how many items the standard bundle needs. The spring remix pool includes Spring Onion and still needs 4. Joja Community Development is five projects: Minecarts 15,000g, Panning 20,000g, Bridge 25,000g, Greenhouse 35,000g, Bus 40,000g. The checklist stores them separately from the standard bundles.

## Still not fully verified

- Dialogue inside heart events is a short factual summary, not a line-by-line transcript. The group 10-heart scene is the shared Bouquet text, not each bachelor's version of the pool game.
- A gift with no universal category and no personal bullet keeps its previous reaction. New items that were never in the gift file, such as Honey, were not given a full new row.
- Crab-pot fish do not use the fishing minigame, so difficulty is 0 and the behavior label is "Crab Pot". That is a label, not a minigame pattern.
- Remix entries are every option on the Remixed Bundles page. The game picks one per slot. The list shows the options. It does not know which one a save rolled.
- Joja membership price and the movie-theater purchase are not in the five-row Community Development table, so they are not checklist items.
- Strange Doll 1 and 2 still use those file names. The wiki titles are Strange Doll (green) and Strange Doll (yellow).
- Fishing level is still only set when a page says "Requires level N".
- Ginger Island all-year fishing stays a location line. The season icons are the valley seasons. Crop notes cover Pineapple, Taro Root, and rice. Other island-only timing is not a separate season flag.
- No emulator or device was available, so these screens were not opened and this pass has no screenshots. Image coverage was checked by resource name.

## How to repeat the check

```bash
python3 tools/check_reference_data.py
./gradlew testDebugUnitTest
```

The Python check fails if a referenced image is missing and not listed as a known placeholder, if Corn, Sunflower, or Wheat omit fall, if Blue Jazz's silver energy drifts from the wiki formula, if Bream or Anchovy regress, if a gift name is duplicated, if a bundle guide is not text, if the River Fish reward is not Deluxe Bait, if Fiber, Tea, or Mixed Seeds are missing, if Lobster is not a 5 XP crab-pot fish, if Leo or Krobus have no heart events, if Penny does not love Emerald, if Alex does not dislike Alamite, if the Joja bus or the Spring Onion remix option is missing, or if Earth Crystal's mine range is not floors 1-39.
