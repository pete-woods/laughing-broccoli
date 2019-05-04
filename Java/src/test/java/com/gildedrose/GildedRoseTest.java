package com.gildedrose;

import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.api.ProxyableListAssert;
import org.junit.Rule;
import org.junit.Test;

import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class GildedRoseTest {

    private static final Item[] ALL_ITEMS = {
            new Item("+5 Dexterity Vest", 10, 20), //
            new Item("Aged Brie", 2, 0), //
            new Item("Elixir of the Mongoose", 5, 7), //
            new Item("Sulfuras, Hand of Ragnaros", 0, 80), //
            new Item("Sulfuras, Hand of Ragnaros", -1, 80),
            new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
            new Item("Backstage passes to a TAFKAL80ETC concert", 10, 49),
            new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49),
            // this conjured item does not work properly yet
            new Item("Conjured Mana Cake", 3, 6)};

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Item normalItem(int sellIn, int quality) {
        return new Item("Normal Item", sellIn, quality);
    }

    private static Item agedBrie(int sellIn, int quality) {
        return new Item("Aged Brie", sellIn, quality);
    }

    private Helper given() {
        return new Helper(softly);
    }

    /**
     * At the end of each day our system lowers both values for every item.
     * (clarification: it seems to be by one for both within the sell-in period)
     */
    @Test
    public void normalItemsWithinSellInPeriodOneDay() {
        given()
                .items(
                        normalItem(5, 10),
                        normalItem(8, 5)
                )
                .whenDaysPass(1)
                .then(items -> {
                    items.hasSize(2);
                    items.element(0).isEqualToComparingFieldByField(normalItem(4, 9));
                    items.element(1).isEqualToComparingFieldByField(normalItem(7, 4));
                });
    }

    @Test
    public void normalItemsWithinSellInPeriodThreeDays() {
        given()
                .items(
                        normalItem(5, 10),
                        normalItem(8, 5)
                )
                .whenDaysPass(3)
                .then(items -> {
                    items.hasSize(2);
                    items.element(0).isEqualToComparingFieldByField(normalItem(2, 7));
                    items.element(1).isEqualToComparingFieldByField(normalItem(5, 2));
                });
    }

    @Test
    public void normalItemsWithinSellInPeriodAtSellLimit() {
        given()
                .items(
                        normalItem(5, 10),
                        normalItem(8, 5)
                )
                .whenDaysPass(5)
                .then(items -> {
                    items.hasSize(2);
                    items.element(0).isEqualToComparingFieldByField(normalItem(0, 5));
                    items.element(1).isEqualToComparingFieldByField(normalItem(3, 0));
                });
    }

    /**
     * Once the sell by date has passed, quality degrades twice as fast.
     */
    @Test
    public void normalItemsAfterSellInPeriodQualityFaster() {
        given()
                .items(
                        normalItem(0, 10),
                        normalItem(-5, 4)
                )
                .whenDaysPass(1)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(normalItem(-1, 8));
                    items.element(1).isEqualToComparingFieldByField(normalItem(-6, 2));
                });
    }

    /**
     * The quality of an item is never negative.
     */
    @Test
    public void normalItemsNeverNegativeQuality() {
        given()
                .items(
                        normalItem(0, 2),
                        normalItem(0, 3)
                )
                .whenDaysPass(5)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(normalItem(-5, 0));
                    items.element(1).isEqualToComparingFieldByField(normalItem(-5, 0));
                });
    }

    /**
     * "Aged Brie" actually increases in quality the older it gets
     */
    @Test
    public void agedBrieIncreasesInQuality() {
        given()
                .items(
                        agedBrie(5, 2),
                        agedBrie(5, 10)
                )
                .whenDaysPass(1)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(agedBrie(4, 3));
                    items.element(1).isEqualToComparingFieldByField(agedBrie(4, 11));
                });
    }

    @Test
    public void agedBrieIncreasesInQualityThreeDays() {
        given()
                .items(
                        agedBrie(5, 2),
                        agedBrie(5, 10)
                )
                .whenDaysPass(3)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(agedBrie(2, 5));
                    items.element(1).isEqualToComparingFieldByField(agedBrie(2, 13));
                });
    }

    private static class Helper {
        private final JUnitSoftAssertions softly;

        private GildedRose gildedRose;

        public Helper(JUnitSoftAssertions softly) {
            this.softly = softly;
        }

        public Helper items(Item... items) {
            gildedRose = new GildedRose(items);
            return this;
        }

        public Helper whenDaysPass(int days) {
            for (int i = 0; i < days; ++i) {
                gildedRose.updateQuality();
            }
            return this;
        }

        public void then(Consumer<ProxyableListAssert<Item>> consumer) {
            consumer.accept(softly.assertThat(asList(gildedRose.items)));
        }
    }
}
