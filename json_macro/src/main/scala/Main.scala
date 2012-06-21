package com.github.xuwei_k

object Main{
  def main(args:Array[String]){
    val j = J("""
      {
         "a":[ 1 , 2.3 , true , "foo" , null ],
         "b" : []
         "c" : {}
      }
    """)

    println(j)
  }
}

