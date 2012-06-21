package com.github.xuwei_k

// https://groups.google.com/forum/?fromgroups#!topic/scala-user/IKR_4rfbPRs

import net.liftweb.json._
import scala.reflect.makro.Context
import language.experimental.macros

object J {
  def apply(jsonSource: String): JValue = macro applyImpl

  def applyImpl(c: Context)(jsonSource: c.Expr[String]): c.Expr[JValue] = {
    import c.universe._
    import MacroHelper._

    def jvalue2tree(j: JValue): c.Tree = {
      j match {
        case JObject(obj) =>
          val xs = obj.map {
            case JField(k, v) =>
               pair(c)(Literal(Constant(k)), jvalue2tree(v))
          }
          jObject(c)(xs: _*)
        case JArray(arr) =>
          val xs = arr.map(x => jvalue2tree(x))
          jArray(c)(xs: _*)
        case JNull =>
          c.reify(JNull).tree
        case JString(s) =>
          jString(c)(s)
        case JDouble(n) =>
          jDouble(c)(n)
        case JInt(n) =>
          jInt(c)(n)
        case JBool(b) =>
          jBool(c)(b)
        case JNothing =>
          c.reify(JNothing).tree
      }
    }

    val Literal(Constant(s_jsonSource: String)) = jsonSource.tree
    val jValue = parse(s_jsonSource)

    c.Expr[JValue](jvalue2tree(jValue))
  }
}

object MacroHelper {
  def list(c: Context)(xs: c.Tree*) = {
    import c.universe._
    val Apply(fun, _) = reify(List(0)).tree
    Apply.apply(fun, xs.toList)
  }

  def jArray(c: Context)(xs: c.Tree*) = {
    import c.universe._
    val Apply(fun, _) = reify(JArray(Nil)).tree
    Apply.apply(fun, list(c)(xs: _*) :: Nil)
  }

  def pair(c: Context)(_1: c.Tree, _2: c.Tree) = {
    import c.universe._
    val Apply(fun, _) = reify((null, null)).tree
    Apply.apply(fun, _1 :: _2 :: Nil)
  }

  def jObject(c: Context)(xs: c.Tree*) = {
    import c.universe._
    val Apply(fun, _) = reify(JObject()).tree
    Apply.apply(fun, xs.toList)
  }

  def jString(c: Context)(s: String) = {
    import c.universe._
    val Apply(fun, _) = reify(JString("")).tree
    Apply.apply(fun, c.literal(s).tree :: Nil)
  }

  def jDouble(c: Context)(n: Double) = {
    import c.universe._
    val Apply(fun, _) = reify(JDouble(0.0)).tree
    Apply.apply(fun, c.literal(n).tree :: Nil)
  }

  def bigInt(c: Context)(n: BigInt) = {
    import c.universe._
    val Apply(fun, _) = reify(BigInt("0")).tree
    Apply.apply(fun, c.literal(n.toString).tree :: Nil)
  }

  def jInt(c: Context)(n: BigInt) = {
    import c.universe._
    val Apply(fun, _) = reify(JInt(0)).tree
    Apply.apply(fun, bigInt(c)(n) :: Nil)
  }

  def jBool(c: Context)(b: Boolean) =
    if (b) {
      c.reify(JBool(true)).tree
    } else {
      c.reify(JBool(false)).tree
    }

}

