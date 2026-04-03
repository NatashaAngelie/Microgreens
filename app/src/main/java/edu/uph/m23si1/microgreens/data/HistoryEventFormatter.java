package edu.uph.m23si1.microgreens.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class HistoryEventFormatter {

    private HistoryEventFormatter() {}

    /** Contoh: {@code 11 September 2025 | 11.03.05 WIB} */
    public static String formatLine(long millis) {
        SimpleDateFormat df = new SimpleDateFormat("d MMMM yyyy | HH.mm.ss", Locale.ENGLISH);
        return df.format(new Date(millis)) + " WIB";
    }

    /** Satu baris untuk kartu: tanggal + jam ringkas */
    public static String formatCardActivity(long millis) {
        SimpleDateFormat df = new SimpleDateFormat("d MMMM yyyy HH.mm.ss", Locale.ENGLISH);
        return df.format(new Date(millis)) + " WIB";
    }
}
