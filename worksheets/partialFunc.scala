def sum(a: Int, b: Int, c: Int) = a + b + c

val sum3 = sum _

sum(1, 2, 3) // 6: Int

val sumC = sum(1, 2, _: Int)

sumC(3) // 6: Int
