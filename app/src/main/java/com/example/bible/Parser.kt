package com.example.bible

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import org.jsoup.Jsoup
import java.io.IOException
import java.sql.SQLDataException

//성경을 파싱하는 클래스 최초한번만 실행한다
class Parser {
    var html = "https://www.bible.com/ko/bible/88"
    var partName =
        "Gen   Exo   Lev   Num   Deu   Jos   Jdg   Rut   1Sa   2Sa   1Ki   2Ki   1Ch   2Ch   Ezr   Neh   Est   Job   Psa   Pro   Ecc   SNG   Isa   Jer   Lam   Ezk   Dan   Hos   Jol   Amo   Oba   Jon   Mic   Nam   Hab   Zep   Hag   Zec   Mal   Mat   Mrk   Luk   Jhn   Act   Rom   1Co   2Co   Gal   Eph   Php   Col   1Th   2Th   1Ti   2Ti   Tit   Phm   Heb   Jas   1Pe   2Pe   1Jn   2Jn   3Jn   Jud   Rev"

    fun htmlParser(context: Context) {
        val dbHelper = DBHelper(context)
        val list = partName.replace("""\s\s\s""".toRegex(), "-").split("-")
        Thread(Runnable {
            try {
                var id = 1001000
                val tmp = Jsoup.connect("$html/gen.1").get().select("div.p > span")[1].text()
                for (i in list) {
                    var num = 1
                    println("$html/$i.$num")
                    while (id < 67000000) {
                        val doc = Jsoup.connect("$html/$i.$num").get()
                        val links = doc.select("div.p > span")
                        if (tmp == links[1].text() && num > 1) break
                        for (j in links) {
                            try {
                                j.text()[0]
                                id++
                                dbHelper.addContents(id, j.text().replace("""[0-9]""".toRegex(), ""))
                            } catch (e: IndexOutOfBoundsException) {
                                e.printStackTrace()
                            }
                        }
                        num++
                        id += 1000
                        id /= 1000
                        id *= 1000
                    }
                    id /= 1000000
                    id *= 1000000
                    id += 1001000
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }).start()

    }

}