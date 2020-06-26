

val simple1 = (s:String) => s"simple1=${s}"
val simple2 = (s:String) => s"simple2=${s}"
val simple3 = (s:String) => s"simple3=${s}"

val x = simple1 andThen simple2 andThen simple3
println(x("hello"))


def meth1(s:String) = s"meth1=${s}"
def meth2(s:String) = s"meth2=${s}"
def meth3(s:String) = s"meth3=${s}"

def y = (meth1 _ andThen meth2 _ andThen meth3 _)(_:String)

val t = y("foo")
println(s"the result is: ${t}")