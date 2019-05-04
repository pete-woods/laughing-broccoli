package com.gildedrose;

import java.util.Objects;

class GildedRose {
    public static final String CONJURED = "Conjured Mana Cake";

    public static final String AGED_BRIE = "Aged Brie";

    public static final String BACKSTAGE_PASSES = "Backstage passes to a TAFKAL80ETC concert";

    public static final String SULFURAS = "Sulfuras, Hand of Ragnaros";

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

            if (!name.equals(AGED_BRIE)
                    && !name.equals(BACKSTAGE_PASSES)) {
                if (item.quality > 0) {
                    if (!name.equals(SULFURAS)) {
                        item.quality -= calculateRate(item);
                    }
                }
            } else {
                if (item.quality < 50) {
                    item.quality++;

                    if (name.equals(BACKSTAGE_PASSES)) {
                        if (item.sellIn < 11) {
                            if (item.quality < 50) {
                                item.quality++;
                            }
                        }

                        if (item.sellIn < 6) {
                            if (item.quality < 50) {
                                item.quality++;
                            }
                        }
                    }
                }
            }

            if (!name.equals(SULFURAS)) {
                item.sellIn--;
            }

            if (item.sellIn < 0) {
                if (!name.equals(AGED_BRIE)) {
                    if (!name.equals(BACKSTAGE_PASSES)) {
                        if (item.quality > 0) {
                            if (!name.equals(SULFURAS)) {
                                item.quality -= calculateRate(item);
                            }
                        }
                    } else {
                        item.quality = 0;
                    }
                } else {
                    if (item.quality < 50) {
                        item.quality++;
                    }
                }
            }
        }
    }

    private int calculateRate(Item item) {
        return item.name.equals(CONJURED) ? 2 : 1;
    }
}
