package com.gildedrose;

import java.util.Objects;

class GildedRose {
    public static final String CONJURED = "Conjured Mana Cake";

    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            Objects.requireNonNull(item);

            String name = item.name;
            Objects.requireNonNull(item.name);

            if (!name.equals("Aged Brie")
                    && !name.equals("Backstage passes to a TAFKAL80ETC concert")) {
                if (item.quality > 0) {
                    if (!name.equals("Sulfuras, Hand of Ragnaros")) {
                        item.quality -= calculateRate(item);
                    }
                }
            } else {
                if (item.quality < 50) {
                    item.quality = item.quality + 1;

                    if (name.equals("Backstage passes to a TAFKAL80ETC concert")) {
                        if (item.sellIn < 11) {
                            if (item.quality < 50) {
                                item.quality = item.quality + 1;
                            }
                        }

                        if (item.sellIn < 6) {
                            if (item.quality < 50) {
                                item.quality = item.quality + 1;
                            }
                        }
                    }
                }
            }

            if (!name.equals("Sulfuras, Hand of Ragnaros")) {
                item.sellIn = item.sellIn - 1;
            }

            if (item.sellIn < 0) {
                if (!name.equals("Aged Brie")) {
                    if (!name.equals("Backstage passes to a TAFKAL80ETC concert")) {
                        if (item.quality > 0) {
                            if (!name.equals("Sulfuras, Hand of Ragnaros")) {
                                item.quality -= calculateRate(item);
                            }
                        }
                    } else {
                        item.quality = item.quality - item.quality;
                    }
                } else {
                    if (item.quality < 50) {
                        item.quality = item.quality + 1;
                    }
                }
            }
        }
    }

    private int calculateRate(Item item) {
        return item.name.equals(CONJURED) ? 2 : 1;
    }
}
