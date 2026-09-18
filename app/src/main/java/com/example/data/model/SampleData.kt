package com.example.data.model

object SampleData {
    fun getFull28Students(): List<StudentRecord> {
        return rawStudentTuples.mapIndexed { index, tuple ->
            val rollNo = index + 1
            val name = tuple.first
            val sr = tuple.second
            val subjectData = tuple.third

            val marksMap = StudentRecord.ALL_SUBJECTS.associateWith { subName ->
                val rawTiers = subjectData[subName] ?: listOf(listOf("", "", ""), listOf("", "", ""), listOf("", "", ""))
                val r1 = rawTiers.getOrNull(0) ?: listOf("", "", "")
                val r2 = rawTiers.getOrNull(1) ?: listOf("", "", "")
                val r3 = rawTiers.getOrNull(2) ?: listOf("", "", "")
                SubjectMarks(
                    subjectName = subName,
                    r1UnitTest = SubjectEvaluation(
                        t1 = r1.getOrElse(0) { "" },
                        t2 = r1.getOrElse(1) { "" },
                        t3 = r1.getOrElse(2) { "" }
                    ),
                    r2HalfYearly = SubjectEvaluation(
                        t1 = r2.getOrElse(0) { "" },
                        t2 = r2.getOrElse(1) { "" },
                        t3 = r2.getOrElse(2) { "" }
                    ),
                    r3Annual = SubjectEvaluation(
                        t1 = r3.getOrElse(0) { "" },
                        t2 = r3.getOrElse(1) { "" },
                        t3 = r3.getOrElse(2) { "" }
                    )
                )
            }

            StudentRecord(
                id = 0,
                rollNo = rollNo,
                name = name,
                exam = "",
                sr = sr,
                marks = marksMap
            )
        }
    }

    private fun sub(
        r1: List<String>,
        r2: List<String>,
        r3: List<String>
    ): List<List<String>> = listOf(r1, r2, r3)

    private val rawStudentTuples = listOf(
        Triple("Jyoti Prajapati", "SR-101", mapOf(
            "HINDI" to sub(listOf("45","44",""), listOf("35","41",""), listOf("49","43","")),
            "ENGLISH" to sub(listOf("45","46",""), listOf("47","43",""), listOf("48","35","")),
            "MATH" to sub(listOf("44","42",""), listOf("49","19",""), listOf("74","18","")),
            "SCIENCE" to sub(listOf("44","46",""), listOf("89","",""), listOf("82","","")),
            "SANSKRIT" to sub(listOf("48","47",""), listOf("84","",""), listOf("99","","")),
            "SOCIAL SCIENCE" to sub(listOf("46","47",""), listOf("99","",""), listOf("96","","")),
            "G.K." to sub(listOf("46","46",""), listOf("79","",""), listOf("95","","")),
            "ART" to sub(listOf("44","40",""), listOf("93","",""), listOf("89","","")),
            "P.T." to sub(listOf("48","49",""), listOf("86","",""), listOf("89","","")),
            "COMPUTER" to sub(listOf("46","47",""), listOf("90","",""), listOf("95","",""))
        )),
        Triple("Kumkum Bharati", "SR-102", mapOf(
            "HINDI" to sub(listOf("30","27",""), listOf("23","21",""), listOf("17","24","")),
            "ENGLISH" to sub(listOf("25","18",""), listOf("20","24",""), listOf("27","19","")),
            "MATH" to sub(listOf("19","21",""), listOf("21","17",""), listOf("36","17","")),
            "SCIENCE" to sub(listOf("23","22",""), listOf("39","",""), listOf("48","","")),
            "SANSKRIT" to sub(listOf("38","37",""), listOf("34","",""), listOf("35","","")),
            "SOCIAL SCIENCE" to sub(listOf("22","25",""), listOf("18","",""), listOf("67","","")),
            "G.K." to sub(listOf("33","30",""), listOf("56","",""), listOf("60","","")),
            "ART" to sub(listOf("42","40",""), listOf("80","",""), listOf("82","","")),
            "P.T." to sub(listOf("38","39",""), listOf("81","",""), listOf("83","","")),
            "COMPUTER" to sub(listOf("25","23",""), listOf("35","",""), listOf("70","",""))
        )),
        Triple("Ananya Singh", "SR-103", mapOf(
            "HINDI" to sub(listOf("35","36",""), listOf("35","39",""), listOf("36","39","")),
            "ENGLISH" to sub(listOf("34","35",""), listOf("29","29",""), listOf("44","35","")),
            "MATH" to sub(listOf("24","25",""), listOf("22","17",""), listOf("42","14","")),
            "SCIENCE" to sub(listOf("24","23",""), listOf("47","",""), listOf("52","","")),
            "SANSKRIT" to sub(listOf("42","45",""), listOf("52","",""), listOf("91","","")),
            "SOCIAL SCIENCE" to sub(listOf("35","33",""), listOf("36","",""), listOf("73","","")),
            "G.K." to sub(listOf("34","32",""), listOf("64","",""), listOf("85","","")),
            "ART" to sub(listOf("39","35",""), listOf("80","",""), listOf("88","","")),
            "P.T." to sub(listOf("37","38",""), listOf("82","",""), listOf("86","","")),
            "COMPUTER" to sub(listOf("34","33",""), listOf("84","",""), listOf("72","",""))
        )),
        Triple("Sanjana", "SR-104", mapOf(
            "HINDI" to sub(listOf("28","27",""), listOf("17","17",""), listOf("17","25","")),
            "ENGLISH" to sub(listOf("25","18",""), listOf("17","17",""), listOf("17","17","")),
            "MATH" to sub(listOf("19","19",""), listOf("24","16",""), listOf("37","15","")),
            "SCIENCE" to sub(listOf("20","19",""), listOf("34","",""), listOf("51","","")),
            "SANSKRIT" to sub(listOf("24","17",""), listOf("33","",""), listOf("35","","")),
            "SOCIAL SCIENCE" to sub(listOf("20","21",""), listOf("34","",""), listOf("70","","")),
            "G.K." to sub(listOf("30","29",""), listOf("57","",""), listOf("50","","")),
            "ART" to sub(listOf("40","40",""), listOf("60","",""), listOf("90","","")),
            "P.T." to sub(listOf("38","33",""), listOf("83","",""), listOf("81","","")),
            "COMPUTER" to sub(listOf("20","19",""), listOf("34","",""), listOf("73","",""))
        )),
        Triple("Pankaj Sharma", "SR-105", mapOf(
            "HINDI" to sub(listOf("38","40",""), listOf("22","35",""), listOf("45","45","")),
            "ENGLISH" to sub(listOf("40","44",""), listOf("37","28",""), listOf("45","25","")),
            "MATH" to sub(listOf("21","21",""), listOf("25","17",""), listOf("44","17","")),
            "SCIENCE" to sub(listOf("24","22",""), listOf("48","",""), listOf("53","","")),
            "SANSKRIT" to sub(listOf("42","46",""), listOf("73","",""), listOf("92","","")),
            "SOCIAL SCIENCE" to sub(listOf("21","21",""), listOf("35","",""), listOf("59","","")),
            "G.K." to sub(listOf("41","43",""), listOf("70","",""), listOf("85","","")),
            "ART" to sub(listOf("42","40",""), listOf("78","",""), listOf("85","","")),
            "P.T." to sub(listOf("41","40",""), listOf("85","",""), listOf("83","","")),
            "COMPUTER" to sub(listOf("21","22",""), listOf("68","",""), listOf("56","",""))
        )),
        Triple("PRINCE I", "SR-106", mapOf(
            "HINDI" to sub(listOf("34","33",""), listOf("19","19",""), listOf("17","18","")),
            "ENGLISH" to sub(listOf("30","27",""), listOf("20","17",""), listOf("25","17","")),
            "MATH" to sub(listOf("20","22",""), listOf("19","16",""), listOf("34","15","")),
            "SCIENCE" to sub(listOf("24","22",""), listOf("52","",""), listOf("54","","")),
            "SANSKRIT" to sub(listOf("33","32",""), listOf("34","",""), listOf("54","","")),
            "SOCIAL SCIENCE" to sub(listOf("19","21",""), listOf("34","",""), listOf("60","","")),
            "G.K." to sub(listOf("31","30",""), listOf("51","",""), listOf("80","","")),
            "ART" to sub(listOf("38","40",""), listOf("78","",""), listOf("80","","")),
            "P.T." to sub(listOf("35","40",""), listOf("82","",""), listOf("81","","")),
            "COMPUTER" to sub(listOf("25","22",""), listOf("34","",""), listOf("57","",""))
        )),
        Triple("PRIYANSHU", "SR-107", mapOf(
            "HINDI" to sub(listOf("45","48",""), listOf("29","34",""), listOf("42","44","")),
            "ENGLISH" to sub(listOf("42","43",""), listOf("46","42",""), listOf("41","36","")),
            "MATH" to sub(listOf("39","40",""), listOf("53","19",""), listOf("61","19","")),
            "SCIENCE" to sub(listOf("41","42",""), listOf("70","",""), listOf("79","","")),
            "SANSKRIT" to sub(listOf("45","46",""), listOf("60","",""), listOf("81","","")),
            "SOCIAL SCIENCE" to sub(listOf("38","40",""), listOf("73","",""), listOf("90","","")),
            "G.K." to sub(listOf("39","38",""), listOf("92","",""), listOf("88","","")),
            "ART" to sub(listOf("44","43",""), listOf("85","",""), listOf("85","","")),
            "P.T." to sub(listOf("41","43",""), listOf("84","",""), listOf("89","","")),
            "COMPUTER" to sub(listOf("38","41",""), listOf("72","",""), listOf("89","",""))
        )),
        Triple("ANISH SINGH", "SR-108", mapOf(
            "HINDI" to sub(listOf("45","40",""), listOf("37","44",""), listOf("47","49","")),
            "ENGLISH" to sub(listOf("43","45",""), listOf("46","45",""), listOf("44","41","")),
            "MATH" to sub(listOf("44","45",""), listOf("76","19",""), listOf("78","19","")),
            "SCIENCE" to sub(listOf("42","43",""), listOf("88","",""), listOf("88","","")),
            "SANSKRIT" to sub(listOf("45","45",""), listOf("80","",""), listOf("97","","")),
            "SOCIAL SCIENCE" to sub(listOf("43","45",""), listOf("87","",""), listOf("87","","")),
            "G.K." to sub(listOf("46","47",""), listOf("95","",""), listOf("92","","")),
            "ART" to sub(listOf("44","45",""), listOf("90","",""), listOf("82","","")),
            "P.T." to sub(listOf("42","43",""), listOf("86","",""), listOf("89","","")),
            "COMPUTER" to sub(listOf("42","44",""), listOf("84","",""), listOf("83","",""))
        )),
        Triple("YURAJ SINGH", "SR-109", mapOf(
            "HINDI" to sub(listOf("17","17",""), listOf("21","30",""), listOf("46","31","")),
            "ENGLISH" to sub(listOf("37","36",""), listOf("40","39",""), listOf("19","20","")),
            "MATH" to sub(listOf("39","40",""), listOf("50","18",""), listOf("60","18","")),
            "SCIENCE" to sub(listOf("32","35",""), listOf("39","",""), listOf("59","","")),
            "SANSKRIT" to sub(listOf("41","42",""), listOf("49","",""), listOf("39","","")),
            "SOCIAL SCIENCE" to sub(listOf("28","27",""), listOf("34","",""), listOf("80","","")),
            "G.K." to sub(listOf("17","17",""), listOf("93","",""), listOf("72","","")),
            "ART" to sub(listOf("38","40",""), listOf("78","",""), listOf("90","","")),
            "P.T." to sub(listOf("39","43",""), listOf("82","",""), listOf("86","","")),
            "COMPUTER" to sub(listOf("39","38",""), listOf("69","",""), listOf("78","",""))
        )),
        Triple("PRINCE II", "SR-110", mapOf(
            "HINDI" to sub(listOf("37","38",""), listOf("19","34",""), listOf("41","24","")),
            "ENGLISH" to sub(listOf("35","42",""), listOf("31","32",""), listOf("37","27","")),
            "MATH" to sub(listOf("25","28",""), listOf("22","16",""), listOf("30","16","")),
            "SCIENCE" to sub(listOf("25","26",""), listOf("49","",""), listOf("50","","")),
            "SANSKRIT" to sub(listOf("46","47",""), listOf("56","",""), listOf("62","","")),
            "SOCIAL SCIENCE" to sub(listOf("22","25",""), listOf("34","",""), listOf("79","","")),
            "G.K." to sub(listOf("42","43",""), listOf("78","",""), listOf("68","","")),
            "ART" to sub(listOf("35","39",""), listOf("67","",""), listOf("80","","")),
            "P.T." to sub(listOf("33","39",""), listOf("83","",""), listOf("81","","")),
            "COMPUTER" to sub(listOf("27","27",""), listOf("44","",""), listOf("77","",""))
        )),
        Triple("SUNNY SINGH", "SR-111", mapOf(
            "HINDI" to sub(listOf("36","35",""), listOf("19","19",""), listOf("28","23","")),
            "ENGLISH" to sub(listOf("31","24",""), listOf("38","25",""), listOf("17","17","")),
            "MATH" to sub(listOf("25","28",""), listOf("29","17",""), listOf("39","17","")),
            "SCIENCE" to sub(listOf("26","24",""), listOf("37","",""), listOf("48","","")),
            "SANSKRIT" to sub(listOf("43","44",""), listOf("33","",""), listOf("51","","")),
            "SOCIAL SCIENCE" to sub(listOf("23","22",""), listOf("34","",""), listOf("55","","")),
            "G.K." to sub(listOf("41","42",""), listOf("88","",""), listOf("66","","")),
            "ART" to sub(listOf("38","40",""), listOf("70","",""), listOf("78","","")),
            "P.T." to sub(listOf("38","39",""), listOf("80","",""), listOf("81","","")),
            "COMPUTER" to sub(listOf("21","22",""), listOf("34","",""), listOf("56","",""))
        )),
        Triple("DEEWAKAR SINGH", "SR-112", mapOf(
            "HINDI" to sub(listOf("36","37",""), listOf("21","21",""), listOf("32","27","")),
            "ENGLISH" to sub(listOf("29","20",""), listOf("27","17",""), listOf("28","25","")),
            "MATH" to sub(listOf("26","27",""), listOf("18","17",""), listOf("39","16","")),
            "SCIENCE" to sub(listOf("21","26",""), listOf("39","",""), listOf("49","","")),
            "SANSKRIT" to sub(listOf("41","42",""), listOf("36","",""), listOf("36","","")),
            "SOCIAL SCIENCE" to sub(listOf("23","22",""), listOf("34","",""), listOf("58","","")),
            "G.K." to sub(listOf("36","37",""), listOf("75","",""), listOf("66","","")),
            "ART" to sub(listOf("39","40",""), listOf("70","",""), listOf("80","","")),
            "P.T." to sub(listOf("39","38",""), listOf("81","",""), listOf("82","","")),
            "COMPUTER" to sub(listOf("24","23",""), listOf("34","",""), listOf("59","",""))
        )),
        Triple("ANISH KUMAR", "SR-113", mapOf(
            "HINDI" to sub(listOf("36","33",""), listOf("20","31",""), listOf("29","33","")),
            "ENGLISH" to sub(listOf("40","38",""), listOf("43","39",""), listOf("37","31","")),
            "MATH" to sub(listOf("38","40",""), listOf("40","18",""), listOf("63","19","")),
            "SCIENCE" to sub(listOf("36","37",""), listOf("42","",""), listOf("59","","")),
            "SANSKRIT" to sub(listOf("39","38",""), listOf("43","",""), listOf("47","","")),
            "SOCIAL SCIENCE" to sub(listOf("29","29",""), listOf("34","",""), listOf("61","","")),
            "G.K." to sub(listOf("42","41",""), listOf("91","",""), listOf("80","","")),
            "ART" to sub(listOf("41","42",""), listOf("85","",""), listOf("95","","")),
            "P.T." to sub(listOf("41","43",""), listOf("85","",""), listOf("87","","")),
            "COMPUTER" to sub(listOf("37","38",""), listOf("66","",""), listOf("66","",""))
        )),
        Triple("MD. IRSHAD", "SR-114", mapOf(
            "HINDI" to sub(listOf("43","41",""), listOf("29","37",""), listOf("46","38","")),
            "ENGLISH" to sub(listOf("43","42",""), listOf("43","39",""), listOf("44","32","")),
            "MATH" to sub(listOf("41","43",""), listOf("51","19",""), listOf("62","19","")),
            "SCIENCE" to sub(listOf("40","42",""), listOf("79","",""), listOf("70","","")),
            "SANSKRIT" to sub(listOf("45","46",""), listOf("78","",""), listOf("95","","")),
            "SOCIAL SCIENCE" to sub(listOf("41","40",""), listOf("86","",""), listOf("89","","")),
            "G.K." to sub(listOf("44","43",""), listOf("78","",""), listOf("87","","")),
            "ART" to sub(listOf("42","43",""), listOf("82","",""), listOf("90","","")),
            "P.T." to sub(listOf("43","42",""), listOf("87","",""), listOf("88","","")),
            "COMPUTER" to sub(listOf("40","41",""), listOf("79","",""), listOf("85","",""))
        )),
        Triple("LAXMINA GUPTA", "SR-115", mapOf(
            "HINDI" to sub(listOf("45","21",""), listOf("47","46",""), listOf("49","47","")),
            "ENGLISH" to sub(listOf("43","45",""), listOf("47","40",""), listOf("45","33","")),
            "MATH" to sub(listOf("44","45",""), listOf("71","20",""), listOf("68","19","")),
            "SCIENCE" to sub(listOf("43","45",""), listOf("99","",""), listOf("97","","")),
            "SANSKRIT" to sub(listOf("45","46",""), listOf("88","",""), listOf("99","","")),
            "SOCIAL SCIENCE" to sub(listOf("42","41",""), listOf("61","",""), listOf("95","","")),
            "G.K." to sub(listOf("44","17",""), listOf("90","",""), listOf("95","","")),
            "ART" to sub(listOf("42","44",""), listOf("80","",""), listOf("88","","")),
            "P.T." to sub(listOf("44","43",""), listOf("89","",""), listOf("88","","")),
            "COMPUTER" to sub(listOf("42","45",""), listOf("90","",""), listOf("96","",""))
        )),
        Triple("SAMAR GUPTA", "SR-116", mapOf(
            "HINDI" to sub(listOf("39","19",""), listOf("26","32",""), listOf("35","40","")),
            "ENGLISH" to sub(listOf("39","36",""), listOf("36","26",""), listOf("36","30","")),
            "MATH" to sub(listOf("34","33",""), listOf("36","18",""), listOf("55","17","")),
            "SCIENCE" to sub(listOf("35","38",""), listOf("67","",""), listOf("60","","")),
            "SANSKRIT" to sub(listOf("41","40",""), listOf("70","",""), listOf("91","","")),
            "SOCIAL SCIENCE" to sub(listOf("29","31",""), listOf("34","",""), listOf("74","","")),
            "G.K." to sub(listOf("41","17",""), listOf("88","",""), listOf("63","","")),
            "ART" to sub(listOf("40","41",""), listOf("80","",""), listOf("85","","")),
            "P.T." to sub(listOf("39","42",""), listOf("85","",""), listOf("84","","")),
            "COMPUTER" to sub(listOf("38","40",""), listOf("67","",""), listOf("70","",""))
        )),
        Triple("ANSH SINGH", "SR-117", mapOf(
            "HINDI" to sub(listOf("42","41",""), listOf("31","33",""), listOf("19","42","")),
            "ENGLISH" to sub(listOf("41","44",""), listOf("44","39",""), listOf("35","34","")),
            "MATH" to sub(listOf("38","36",""), listOf("40","18",""), listOf("54","18","")),
            "SCIENCE" to sub(listOf("34","36",""), listOf("67","",""), listOf("60","","")),
            "SANSKRIT" to sub(listOf("45","47",""), listOf("55","",""), listOf("66","","")),
            "SOCIAL SCIENCE" to sub(listOf("32","37",""), listOf("62","",""), listOf("63","","")),
            "G.K." to sub(listOf("43","44",""), listOf("75","",""), listOf("65","","")),
            "ART" to sub(listOf("39","40",""), listOf("75","",""), listOf("88","","")),
            "P.T." to sub(listOf("38","41",""), listOf("82","",""), listOf("86","","")),
            "COMPUTER" to sub(listOf("38","36",""), listOf("48","",""), listOf("60","",""))
        )),
        Triple("DIVYA SINGH", "SR-118", mapOf(
            "HINDI" to sub(listOf("35","32",""), listOf("25","23",""), listOf("41","30","")),
            "ENGLISH" to sub(listOf("33","30",""), listOf("29","29",""), listOf("43","25","")),
            "MATH" to sub(listOf("30","31",""), listOf("20","17",""), listOf("49","16","")),
            "SCIENCE" to sub(listOf("29","33",""), listOf("49","",""), listOf("54","","")),
            "SANSKRIT" to sub(listOf("44","44",""), listOf("53","",""), listOf("94","","")),
            "SOCIAL SCIENCE" to sub(listOf("29","30",""), listOf("40","",""), listOf("57","","")),
            "G.K." to sub(listOf("30","29",""), listOf("64","",""), listOf("52","","")),
            "ART" to sub(listOf("41","45",""), listOf("94","",""), listOf("95","","")),
            "P.T." to sub(listOf("38","40",""), listOf("81","",""), listOf("84","","")),
            "COMPUTER" to sub(listOf("33","35",""), listOf("51","",""), listOf("58","",""))
        )),
        Triple("KRISHNA PRAJAPATI", "SR-119", mapOf(
            "HINDI" to sub(listOf("40","41",""), listOf("23","29",""), listOf("37","28","")),
            "ENGLISH" to sub(listOf("40","38",""), listOf("29","28",""), listOf("39","27","")),
            "MATH" to sub(listOf("40","38",""), listOf("35","17",""), listOf("60","17","")),
            "SCIENCE" to sub(listOf("36","39",""), listOf("62","",""), listOf("63","","")),
            "SANSKRIT" to sub(listOf("45","47",""), listOf("55","",""), listOf("76","","")),
            "SOCIAL SCIENCE" to sub(listOf("28","29",""), listOf("34","",""), listOf("58","","")),
            "G.K." to sub(listOf("43","44",""), listOf("76","",""), listOf("56","","")),
            "ART" to sub(listOf("41","40",""), listOf("85","",""), listOf("90","","")),
            "P.T." to sub(listOf("39","41",""), listOf("84","",""), listOf("85","","")),
            "COMPUTER" to sub(listOf("31","34",""), listOf("34","",""), listOf("51","",""))
        )),
        Triple("SABHYA SINGH", "SR-120", mapOf(
            "HINDI" to sub(listOf("35","34",""), listOf("27","26",""), listOf("17","25","")),
            "ENGLISH" to sub(listOf("38","39",""), listOf("33","24",""), listOf("25","23","")),
            "MATH" to sub(listOf("30","29",""), listOf("20","18",""), listOf("41","17","")),
            "SCIENCE" to sub(listOf("28","34",""), listOf("49","",""), listOf("54","","")),
            "SANSKRIT" to sub(listOf("41","40",""), listOf("50","",""), listOf("59","","")),
            "SOCIAL SCIENCE" to sub(listOf("25","30",""), listOf("52","",""), listOf("55","","")),
            "G.K." to sub(listOf("30","29",""), listOf("75","",""), listOf("60","","")),
            "ART" to sub(listOf("38","40",""), listOf("75","",""), listOf("82","","")),
            "P.T." to sub(listOf("40","39",""), listOf("83","",""), listOf("81","","")),
            "COMPUTER" to sub(listOf("31","31",""), listOf("51","",""), listOf("54","",""))
        )),
        Triple("SIDDHARTH SHARMA", "SR-121", mapOf(
            "HINDI" to sub(listOf("17","17",""), listOf("20","20",""), listOf("17","21","")),
            "ENGLISH" to sub(listOf("25","18",""), listOf("24","17",""), listOf("17","17","")),
            "MATH" to sub(listOf("24","19",""), listOf("18","16",""), listOf("35","16","")),
            "SCIENCE" to sub(listOf("26","26",""), listOf("34","",""), listOf("39","","")),
            "SANSKRIT" to sub(listOf("21","20",""), listOf("34","",""), listOf("35","","")),
            "SOCIAL SCIENCE" to sub(listOf("21","22",""), listOf("34","",""), listOf("59","","")),
            "G.K." to sub(listOf("34","17",""), listOf("52","",""), listOf("45","","")),
            "ART" to sub(listOf("33","32",""), listOf("50","",""), listOf("55","","")),
            "P.T." to sub(listOf("37","39",""), listOf("80","",""), listOf("81","","")),
            "COMPUTER" to sub(listOf("26","23",""), listOf("37","",""), listOf("57","",""))
        )),
        Triple("AJIT GUPTA", "SR-122", mapOf(
            "HINDI" to sub(listOf("38","36",""), listOf("21","39",""), listOf("48","26","")),
            "ENGLISH" to sub(listOf("39","35",""), listOf("37","37",""), listOf("30","17","")),
            "MATH" to sub(listOf("40","39",""), listOf("44","18",""), listOf("39","17","")),
            "SCIENCE" to sub(listOf("36","37",""), listOf("64","",""), listOf("63","","")),
            "SANSKRIT" to sub(listOf("45","47",""), listOf("69","",""), listOf("41","","")),
            "SOCIAL SCIENCE" to sub(listOf("29","29",""), listOf("34","",""), listOf("63","","")),
            "G.K." to sub(listOf("41","42",""), listOf("88","",""), listOf("69","","")),
            "ART" to sub(listOf("41","40",""), listOf("85","",""), listOf("80","","")),
            "P.T." to sub(listOf("40","41",""), listOf("85","",""), listOf("82","","")),
            "COMPUTER" to sub(listOf("31","31",""), listOf("34","",""), listOf("60","",""))
        )),
        Triple("ANSHIKA NISHAD", "SR-123", mapOf(
            "HINDI" to sub(listOf("34","32",""), listOf("21","21",""), listOf("19","21","")),
            "ENGLISH" to sub(listOf("27","17",""), listOf("17","17",""), listOf("17","17","")),
            "MATH" to sub(listOf("28","26",""), listOf("18","18",""), listOf("37","16","")),
            "SCIENCE" to sub(listOf("28","27",""), listOf("36","",""), listOf("41","","")),
            "SANSKRIT" to sub(listOf("40","39",""), listOf("37","",""), listOf("34","","")),
            "SOCIAL SCIENCE" to sub(listOf("23","22",""), listOf("34","",""), listOf("66","","")),
            "G.K." to sub(listOf("30","28",""), listOf("67","",""), listOf("55","","")),
            "ART" to sub(listOf("40","41",""), listOf("80","",""), listOf("65","","")),
            "P.T." to sub(listOf("38","37",""), listOf("84","",""), listOf("82","","")),
            "COMPUTER" to sub(listOf("25","28",""), listOf("34","",""), listOf("69","",""))
        )),
        Triple("SWETA RAJBHAR", "SR-124", mapOf(
            "HINDI" to sub(listOf("34","32",""), listOf("21","24",""), listOf("31","17","")),
            "ENGLISH" to sub(listOf("24","21",""), listOf("25","21",""), listOf("21","17","")),
            "MATH" to sub(listOf("28","29",""), listOf("22","16",""), listOf("38","18","")),
            "SCIENCE" to sub(listOf("33","33",""), listOf("44","",""), listOf("49","","")),
            "SANSKRIT" to sub(listOf("33","30",""), listOf("57","",""), listOf("34","","")),
            "SOCIAL SCIENCE" to sub(listOf("24","23",""), listOf("34","",""), listOf("70","","")),
            "G.K." to sub(listOf("32","31",""), listOf("49","",""), listOf("51","","")),
            "ART" to sub(listOf("41","42",""), listOf("82","",""), listOf("80","","")),
            "P.T." to sub(listOf("37","39",""), listOf("83","",""), listOf("84","","")),
            "COMPUTER" to sub(listOf("26","28",""), listOf("34","",""), listOf("72","",""))
        )),
        Triple("MD. RAZA", "SR-125", mapOf(
            "HINDI" to sub(listOf("35","36",""), listOf("19","27",""), listOf("42","40","")),
            "ENGLISH" to sub(listOf("35","25",""), listOf("37","34",""), listOf("31","32","")),
            "MATH" to sub(listOf("35","34",""), listOf("24","17",""), listOf("54","19","")),
            "SCIENCE" to sub(listOf("37","40",""), listOf("67","",""), listOf("63","","")),
            "SANSKRIT" to sub(listOf("45","47",""), listOf("34","",""), listOf("49","","")),
            "SOCIAL SCIENCE" to sub(listOf("24","28",""), listOf("34","",""), listOf("78","","")),
            "G.K." to sub(listOf("42","43",""), listOf("88","",""), listOf("75","","")),
            "ART" to sub(listOf("44","43",""), listOf("90","",""), listOf("90","","")),
            "P.T." to sub(listOf("41","43",""), listOf("85","",""), listOf("83","","")),
            "COMPUTER" to sub(listOf("30","31",""), listOf("34","",""), listOf("73","",""))
        )),
        Triple("SAHIBA KHATOON", "SR-126", mapOf(
            "HINDI" to sub(listOf("33","30",""), listOf("21","32",""), listOf("28","23","")),
            "ENGLISH" to sub(listOf("17","17",""), listOf("17","17",""), listOf("17","17","")),
            "MATH" to sub(listOf("27","24",""), listOf("18","18",""), listOf("37","16","")),
            "SCIENCE" to sub(listOf("29","28",""), listOf("35","",""), listOf("44","","")),
            "SANSKRIT" to sub(listOf("33","17",""), listOf("34","",""), listOf("34","","")),
            "SOCIAL SCIENCE" to sub(listOf("22","21",""), listOf("34","",""), listOf("70","","")),
            "G.K." to sub(listOf("30","28",""), listOf("67","",""), listOf("61","","")),
            "ART" to sub(listOf("44","43",""), listOf("93","",""), listOf("80","","")),
            "P.T." to sub(listOf("37","38",""), listOf("81","",""), listOf("83","","")),
            "COMPUTER" to sub(listOf("24","23",""), listOf("34","",""), listOf("79","",""))
        )),
        Triple("AJAY CHAUHAN", "SR-127", mapOf(
            "HINDI" to sub(listOf("25","20",""), listOf("19","19",""), listOf("43","30","")),
            "ENGLISH" to sub(listOf("25","21",""), listOf("17","17",""), listOf("32","17","")),
            "MATH" to sub(listOf("25","24",""), listOf("19","16",""), listOf("39","16","")),
            "SCIENCE" to sub(listOf("31","30",""), listOf("46","",""), listOf("49","","")),
            "SANSKRIT" to sub(listOf("42","46",""), listOf("47","",""), listOf("65","","")),
            "SOCIAL SCIENCE" to sub(listOf("23","24",""), listOf("34","",""), listOf("50","","")),
            "G.K." to sub(listOf("35","36",""), listOf("50","",""), listOf("59","","")),
            "ART" to sub(listOf("40","39",""), listOf("80","",""), listOf("75","","")),
            "P.T." to sub(listOf("38","39",""), listOf("85","",""), listOf("80","","")),
            "COMPUTER" to sub(listOf("21","23",""), listOf("34","",""), listOf("56","",""))
        )),
        Triple("MD SARFRAJ", "SR-128", mapOf(
            "HINDI" to sub(listOf("35","38",""), listOf("21","38",""), listOf("40","27","")),
            "ENGLISH" to sub(listOf("38","39",""), listOf("20","20",""), listOf("32","33","")),
            "MATH" to sub(listOf("36","37",""), listOf("30","17",""), listOf("48","17","")),
            "SCIENCE" to sub(listOf("28","37",""), listOf("41","",""), listOf("54","","")),
            "SANSKRIT" to sub(listOf("41","43",""), listOf("50","",""), listOf("39","","")),
            "SOCIAL SCIENCE" to sub(listOf("26","25",""), listOf("34","",""), listOf("69","","")),
            "G.K." to sub(listOf("40","41",""), listOf("65","",""), listOf("68","","")),
            "ART" to sub(listOf("42","41",""), listOf("90","",""), listOf("85","","")),
            "P.T." to sub(listOf("40","39",""), listOf("86","",""), listOf("84","","")),
            "COMPUTER" to sub(listOf("32","34",""), listOf("56","",""), listOf("73","",""))
        ))
    )
}
