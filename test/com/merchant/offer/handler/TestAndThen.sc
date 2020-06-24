

val simple1 = (s:String) => s"simple1=${s}"
val simple2 = (s:String) => s"simple2=${s}"
val simple3 = (s:String) => s"simple3=${s}"

val x = simple1 andThen simple2 andThen simple3
println(x("hello"))