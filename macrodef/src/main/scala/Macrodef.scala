package com.github.xuwei_k

// https://groups.google.com/d/msg/scala-user/IKR_4rfbPRs/upd3JuN3Rd4J

import org.json4s.JsonAST._
import org.json4s.native.JsonParser
import scala.reflect.macros.Context
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
          reify(JNull).tree
        case JString(s) =>
          jString(c)(s)
        case JDouble(n) =>
          jDouble(c)(n)
        case JDecimal(n) =>
          jDecimal(c)(n)
        case JInt(n) =>
          jInt(c)(n)
        case JBool(b) =>
          jBool(c)(b)
        case JNothing =>
          reify(JNothing).tree
      }
    }

    val Literal(Constant(s_jsonSource: String)) = jsonSource.tree
    val jValue = JsonParser.parse(s_jsonSource)

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

  def jDecimal(c: Context)(n: BigDecimal) = {
    import c.universe._
    val Apply(fun, _) = reify(JDecimal(0.0)).tree
    Apply.apply(fun, c.literal(n.toString).tree :: Nil)
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

  def jBool(c: Context)(b: Boolean) = {
    import c.universe._
    if (b) {
      reify(JBool(true)).tree
    } else {
      reify(JBool(false)).tree
    }
  }

}

