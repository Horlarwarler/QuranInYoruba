package com.sadaqaworks.yorubaquran.util

import android.util.Log


class NormalizeSearch {

    private val arabicNormalizer: List<Normalizer> = listOf(
        Normalizer("\u0610", "")//ARABIC SIGN SALLALLAHOU ALAYHE WA SALLAM
        , Normalizer("\u0611", "")//ARABIC SIGN ALAYHE ASSALLAM
        , Normalizer("\u0612", "")//ARABIC SIGN RAHMATULLAH ALAYHE
        , Normalizer("\u0613", "")//ARABIC SIGN RADI ALLAHOU ANHU
        , Normalizer("\u0614", "")//ARABIC SIGN TAKHALLUS

        //Remove koranic anotation
        , Normalizer("\u0615", "")//ARABIC SMALL HIGH TAH
        , Normalizer("\u0616", "")//ARABIC SMALL HIGH LIGATURE ALEF WITH LAM WITH YEH
        , Normalizer("\u0617", "")//ARABIC SMALL HIGH ZAIN
        , Normalizer("\u0618", "")//ARABIC SMALL FATHA
        , Normalizer("\u0619", "")//ARABIC SMALL DAMMA
        , Normalizer("\u061A", "")//ARABIC SMALL KASRA
        , Normalizer("\u06D6", "")//ARABIC SMALL HIGH LIGATURE SAD WITH LAM WITH ALEF MAKSURA
        , Normalizer("\u06D7", "")//ARABIC SMALL HIGH LIGATURE QAF WITH LAM WITH ALEF MAKSURA
        , Normalizer("\u06D8", "")//ARABIC SMALL HIGH MEEM INITIAL FORM
        , Normalizer("\u06D9", "")//ARABIC SMALL HIGH LAM ALEF
        , Normalizer("\u06DA", "")//ARABIC SMALL HIGH JEEM
        , Normalizer("\u06DB", "")//ARABIC SMALL HIGH THREE DOTS
        , Normalizer("\u06DC", "")//ARABIC SMALL HIGH SEEN
        , Normalizer("\u06DD", "")//ARABIC END OF AYAH
        , Normalizer("\u06DE", "")//ARABIC START OF RUB EL HIZB
        , Normalizer("\u06DF", "")//ARABIC SMALL HIGH ROUNDED ZERO
        , Normalizer("\u06E0", "")//ARABIC SMALL HIGH UPRIGHT RECTANGULAR ZERO
        , Normalizer("\u06E1", "")//ARABIC SMALL HIGH DOTLESS HEAD OF KHAH
        , Normalizer("\u06E2", "")//ARABIC SMALL HIGH MEEM ISOLATED FORM
        , Normalizer("\u06E3", "")//ARABIC SMALL LOW SEEN
        , Normalizer("\u06E4", "")//ARABIC SMALL HIGH MADDA
        , Normalizer("\u06E5", "")//ARABIC SMALL WAW
        , Normalizer("\u06E6", "")//ARABIC SMALL YEH
        , Normalizer("\u06E7", "")//ARABIC SMALL HIGH YEH
        , Normalizer("\u06E8", "")//ARABIC SMALL HIGH NOON
        , Normalizer("\u06E9", "")//ARABIC PLACE OF SAJDAH
        , Normalizer("\u06EA", "")//ARABIC EMPTY CENTRE LOW STOP
        , Normalizer("\u06EB", "")//ARABIC EMPTY CENTRE HIGH STOP
        , Normalizer("\u06EC", "")//ARABIC ROUNDED HIGH STOP WITH FILLED CENTRE
        , Normalizer("\u06ED", "")//ARABIC SMALL LOW MEEM

        //Remove tatweel
        , Normalizer("\u0640", "")

        //Remove tashkeel
        , Normalizer("\u064B", "")//ARABIC FATHATAN
        , Normalizer("\u064C", "")//ARABIC DAMMATAN
        , Normalizer("\u064D", "")//ARABIC KASRATAN
        , Normalizer("\u064E", "")//ARABIC FATHA
        , Normalizer("\u064F", "")//ARABIC DAMMA
        , Normalizer("\u0650", "")//ARABIC KASRA
        , Normalizer("\u0651", "")//ARABIC SHADDA
        , Normalizer("\u0652", "")//ARABIC SUKUN
        , Normalizer("\u0653", "")//ARABIC MADDAH ABOVE
        , Normalizer("\u0654", "")//ARABIC HAMZA ABOVE
        , Normalizer("\u0655", "")//ARABIC HAMZA BELOW
        , Normalizer("\u0656", "")//ARABIC SUBSCRIPT ALEF
        , Normalizer("\u0657", "")//ARABIC INVERTED DAMMA
        , Normalizer("\u0658", "")//ARABIC MARK NOON GHUNNA
        , Normalizer("\u0659", "")//ARABIC ZWARAKAY
        , Normalizer("\u065A", "")//ARABIC VOWEL SIGN SMALL V ABOVE
        , Normalizer("\u065B", "")//ARABIC VOWEL SIGN INVERTED SMALL V ABOVE
        , Normalizer("\u065C", "")//ARABIC VOWEL SIGN DOT BELOW
        , Normalizer("\u065D", "")//ARABIC REVERSED DAMMA
        , Normalizer("\u065E", "")//ARABIC FATHA WITH TWO DOTS
        , Normalizer("\u065F", "")//ARABIC WAVY HAMZA BELOW
        , Normalizer("\u0670", "")//ARABIC LETTER SUPERSCRIPT ALEF

        //Replace Waw Hamza Above by Waw
        , Normalizer("\u0624", "\u0648")

        //Replace Ta Marbuta by Ha
        , Normalizer("\u0629", "\u0647")

        //Replace Ya
        // and Ya Hamza Above by Alif Maksura
        , Normalizer("\u064A", "\u0649")
        , Normalizer("\u0626", "\u0649")

        // Replace Alifs with Hamza Above/Below
        // and with Madda Above by Alif
        , Normalizer("\u0622", "\u0627")
        , Normalizer("\u0623", "\u0627")
        , Normalizer("\u0625", "\u0627")

    )
    private val yorubaNormalizer: List<Normalizer> = listOf(
        Normalizer("\u0061","[\u00E1\u00E0\u0061]"),
        Normalizer("\u00E1","[\u00E1\u00E0\u0061]"),
        Normalizer("\u00E0","[\u00E1\u00E0\u0061]"),
        Normalizer("\u0065","[\u0065\u00E9\u00E8\u1EB9]"),
        Normalizer("\u00E9","[\u0065\u00E9\u00E8\u1EB9]"),
        Normalizer("\u00E8","[\u0065\u00E9\u00E8\u1EB9]"),
        Normalizer("\u1EB9","[\u0065\u00E9\u00E8\u1EB9]"),
        Normalizer("\u00ED", "[\u00ED\u00EC\u0069]"),
        Normalizer("\u00EC", "[\u00ED\u00EC\u0069]"),
        Normalizer("\u0069", "[\u00ED\u00EC\u0069]"),
        Normalizer("\u006F","[\u006F\u00F3\u00F2\u1ECD]"),
        Normalizer("\u00F3","[\u006F\u00F3\u00F2\u1ECD]"),
        Normalizer("\u00F2","[\u006F\u00F3\u00F2\u1ECD]"),
        Normalizer("\u1ECD","[\u006F\u00F3\u00F2\u1ECD]"),
        Normalizer("\u0073", "[\u0073\u1E63\u0301]"),
        Normalizer("\u1E63", "[\u0073\u1E63́]"),
        Normalizer("\u0075","[\u0075\u00FA\u00F9]"),
        Normalizer("\u00FA","[\u0075\u00FA\u00F9]"),
        Normalizer("\u00F9","[\u0075\u00FA\u00F9]"),




        )


    fun normalizeArabic(searchText: String): String {
        return  addSymbolToArabic(searchText)
    }

     fun normalizeYoruba(searchText: String): String {
        return replaceYorubaCharacter(searchText)
    }
    private  fun replaceYorubaCharacter(
        input: String,

        ): String{

        val mark = "[\u0301\u0300 ̄‘]*"
        val inputToChar = input.toCharArray()
        val output = StringBuilder()

        for(char in inputToChar){
            val replaceCharacter = replaceLetter(
                char.toString(),
            )
            output.append(replaceCharacter)
            output.append(mark)
        }
        return output.toString()
    }
    private fun replaceLetter(
        char: String,
    ):String{


        val newString = yorubaNormalizer
            .find {
                it.from == char
            }
            ?.to  ?:char
        return  newString
    }

    private fun addSymbolToArabic(
        input: String,
    ): String {

        val normalizedInput = removeSymbolFromArabic(input)
        val inputToChar = normalizedInput.toCharArray()

        val outputString = StringBuilder()
        Log.d("Tag 1",normalizedInput)
        val normalizedString = arabicNormalizer
            .map {
                it.from
            }
            .toString()+"*"


        for( eachChar in inputToChar){
            outputString.append(eachChar)
            outputString.append(normalizedString);
        }
        Log.d("Tag 2",outputString.toString())
        return outputString.toString()

    }

    private fun removeSymbolFromArabic(
        input: String,
        ): String{
        val firstIndex = arabicNormalizer[0]
        var output = input.replace(firstIndex.from, firstIndex.to)

        arabicNormalizer
            .takeLast(arabicNormalizer.size-1)
            .forEach {
                output = output.replace(it.from, it.to,true)

                Log.d("Input Tag " ,"input is $input output is $output")

            }
        return  output

    }

     fun getArabicMarks(): String {
        val marks = arabicNormalizer
            .map {
                it.from
            }
            .toString()

        return marks
    }

    fun getYorubaMarks(): String {
        val marks = yorubaNormalizer
            .map {
                it.from
            }
            .toString() + "+"

        return marks
    }

    companion object{

    }
}

data class  Normalizer ( val  from:  String ,  val  to:  String )