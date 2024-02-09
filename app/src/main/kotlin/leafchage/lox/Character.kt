package leafchage.lox

fun Char.isDigit() = '0' <= this && this <= '9'

fun Char.isAlphaNumeric() = this.isDigit() || this.isAlpha()

fun Char.isAlpha() = this.isLowerLetter() || this.isUpperLetter() || this == '_'

fun Char.isLowerLetter() = 'a' <= this && this <= 'z'

fun Char.isUpperLetter() = 'A' <= this && this <= 'Z'
