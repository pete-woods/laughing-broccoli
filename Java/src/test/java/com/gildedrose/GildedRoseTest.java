package com.gildedrose;

import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.api.ProxyableListAssert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class GildedRoseTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Item normalItem(int sellIn, int quality) {
        return new Item("Normal Item", sellIn, quality);
    }

    private static Item agedBrie(int sellIn, int quality) {
        return new Item("Aged Brie", sellIn, quality);
    }

    private static Item sulfuras(int sellIn, int quality) {
        return new Item("Sulfuras, Hand of Ragnaros", sellIn, quality);
    }

    private static Item backstagePass(int sellIn, int quality) {
        return new Item("Backstage passes to a TAFKAL80ETC concert", sellIn, quality);
    }

    private static Item conjured(int sellIn, int quality) {
        return new Item("Conjured Mana Cake", sellIn, quality);
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

    /**
     * The quality of an item is never more than 50.
     */
    @Test
    public void agedBrieNoMoreThanFiftyQuality() {
        given()
                .items(
                        agedBrie(100, 48),
                        agedBrie(100, 49),
                        agedBrie(100, 50)
                )
                .whenDaysPass(2)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(agedBrie(98, 50));
                    items.element(1).isEqualToComparingFieldByField(agedBrie(98, 50));
                    items.element(2).isEqualToComparingFieldByField(agedBrie(98, 50));
                });
    }

    /**
     * Unclear what the system is supposed to do with invalid items.
     */
    @Ignore
    @Test
    public void agedBrieEnteredWithMoreThanFiftyQuality() {
        given()
                .items(
                        agedBrie(100, 51)
                )
                .whenDaysPass(1)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(agedBrie(99, 51));
                });
    }


    /**
     * "Sulfuras", being a legendary item, never has to be sold or decreases in quality.
     * <p>
     * Just for clarification, an item can never have its quality increase above 50,
     * however "Sulfuras" is a legendary item and as such its quality is 80 and it
     * never alters.
     * <p>
     * TODO: Unclear what we should do with Sulfuras with wrong quality, added assertion for current behaviour
     */
    @Test
    public void sulfurasNeverChanges() {
        given()
                .items(
                        sulfuras(10, 10),
                        sulfuras(20, -5),
                        sulfuras(30, 80),
                        sulfuras(-10, -10),
                        sulfuras(-20, 5),
                        sulfuras(-30, 80)
                )
                .whenDaysPass(10)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(sulfuras(10, 10));
                    items.element(1).isEqualToComparingFieldByField(sulfuras(20, -5));
                    items.element(2).isEqualToComparingFieldByField(sulfuras(30, 80));
                    items.element(3).isEqualToComparingFieldByField(sulfuras(-10, -10));
                    items.element(4).isEqualToComparingFieldByField(sulfuras(-20, 5));
                    items.element(5).isEqualToComparingFieldByField(sulfuras(-30, 80));
                });
    }

    /**
     * "Backstage passes", like aged brie, increases in quality as its sell-in
     * value approaches; quality increases by 2 when there are 10 days or less
     * and by 3 when there are 5 days or less but quality drops to 0 after the
     * concert.
     */
    @Test
    public void backstagePassesIncreaseInQuality() {
        given()
                .items(
                        backstagePass(15, 10),
                        backstagePass(10, 10),
                        backstagePass(5, 10)
                )
                .whenDaysPass(1)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(backstagePass(14, 11));
                    items.element(1).isEqualToComparingFieldByField(backstagePass(9, 12));
                    items.element(2).isEqualToComparingFieldByField(backstagePass(4, 13));
                });
    }

    @Test
    public void backstagePassesIncreaseInQualityThreeDays() {
        given()
                .items(
                        backstagePass(15, 10),
                        backstagePass(10, 10),
                        backstagePass(5, 10)
                )
                .whenDaysPass(3)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(backstagePass(12, 13));
                    items.element(1).isEqualToComparingFieldByField(backstagePass(7, 16));
                    items.element(2).isEqualToComparingFieldByField(backstagePass(2, 19));
                });
    }

    @Test
    public void backstagePassesNoMoreThanFiftyQuality() {
        given()
                .items(
                        backstagePass(15, 50),
                        backstagePass(10, 49),
                        backstagePass(5, 48)
                )
                .whenDaysPass(1)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(backstagePass(14, 50));
                    items.element(1).isEqualToComparingFieldByField(backstagePass(9, 50));
                    items.element(2).isEqualToComparingFieldByField(backstagePass(4, 50));
                });
    }

    @Test
    public void backstagePassesQualityDropsToZero() {
        given()
                .items(
                        backstagePass(0, 50),
                        backstagePass(1, 50)
                )
                .whenDaysPass(1)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(backstagePass(-1, 0));
                    items.element(1).isEqualToComparingFieldByField(backstagePass(0, 50));
                });
        given()
                .items(
                        backstagePass(5, 50),
                        backstagePass(4, 50),
                        backstagePass(3, 50),
                        backstagePass(2, 50),
                        backstagePass(1, 50)
                )
                .whenDaysPass(6)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(backstagePass(-1, 0));
                    items.element(1).isEqualToComparingFieldByField(backstagePass(-2, 0));
                    items.element(2).isEqualToComparingFieldByField(backstagePass(-3, 0));
                    items.element(3).isEqualToComparingFieldByField(backstagePass(-4, 0));
                    items.element(4).isEqualToComparingFieldByField(backstagePass(-5, 0));
                });
    }

    /**
     * "Conjured" items degrade in quality twice as fast as normal items
     */
    @Ignore("Conjured items need implementing")
    @Test
    public void conjuredItemsDegradeTwiceAsFast() {
        given()
                .items(
                        conjured(10, 10),
                        conjured(5, 10),
                        conjured(-5, 10),
                        conjured(-10, 10)
                )
                .whenDaysPass(1)
                .then(items -> {
                    items.element(0).isEqualToComparingFieldByField(conjured(9, 8));
                    items.element(1).isEqualToComparingFieldByField(conjured(4, 8));
                    items.element(2).isEqualToComparingFieldByField(conjured(-6, 6));
                    items.element(3).isEqualToComparingFieldByField(conjured(-11, 6));
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
