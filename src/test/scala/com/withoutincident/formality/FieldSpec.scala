package com.withoutincident
package formality

import scala.xml.Elem

import org.specs2.mutable._
import org.specs2.execute._

import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.util._

import Formality._

trait SessionContext {
  val session = new LiftSession("", StringHelpers.randomString(20), Empty)
}
trait SContext extends Around with SessionContext {
  def around[T <% Result](t: =>T) = {
    S.initIfUninitted(session) {
      AsResult(t)  // execute t inside a http session
    }
  }
}

class FieldSpec extends Specification {
  val templateElement = <div class="boomdayada boomdayadan" data-test-attribute="bam">Here's a test!</div>

  "Simple fields with no initial value" should {
    "only bind the name attribute" in new SContext {
      val formField = field[String](".boomdayada")

      val resultingMarkup = <test-parent>{formField.binder(templateElement)}</test-parent>

      resultingMarkup must \(
        "div",
        "class" -> "boomdayada boomdayadan",
        "data-test-attribute" -> "bam",
        "name" -> ".*"
      )
      (resultingMarkup \ "div" \ "@value").text must_== ""
    }
  }
  
  "Simple fields with an initial value" should {
    "only bind the name and value attributes" in new SContext {
      val formField = field[String](".boomdayada", "Dat value")

      val resultingMarkup = <test-parent>{formField.binder(templateElement)}</test-parent>

      resultingMarkup must \(
        "div",
        "class" -> "boomdayada boomdayadan",
        "data-test-attribute" -> "bam",
        "name" -> ".*",
        "value" -> "Dat value"
      )
    }
  }

  "Regular file upload fields" should {
    "only bind the name and type attributes" in new SContext {
      val formField = fileUploadField(".boomdayada")

      val resultingMarkup = <test-parent>{formField.binder(templateElement)}</test-parent>

      resultingMarkup must \(
        "div",
        "class" -> "boomdayada boomdayadan",
        "data-test-attribute" -> "bam",
        "name" -> ".*",
        "type" -> "file"
      )
    }
  }

  "Typed file upload fields" should {
    "only bind the name and type attributes" in new SContext {
      implicit def fileToObject(fph: FileParamHolder) = Full("boom")

      val formField = typedFileUploadField[String](".boomdayada")

      val resultingMarkup = <test-parent>{formField.binder(templateElement)}</test-parent>

      resultingMarkup must \(
        "div",
        "class" -> "boomdayada boomdayadan",
        "data-test-attribute" -> "bam",
        "name" -> ".*",
        "type" -> "file"
      )
    }
  }
}
