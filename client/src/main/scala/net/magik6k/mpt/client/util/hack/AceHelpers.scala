package net.magik6k.mpt.client.util.hack

import com.scalawarrior.scalajs.ace
import com.scalawarrior.scalajs.ace.Editor
import org.scalajs.dom.raw.Node

import scala.scalajs.js

object AceHelpers {
  @js.native
  implicit class MyAce(a: ace.Ace) extends ace.Ace {
    @js.native
    def edit(el: Node): ace.Editor = js.native
  }
}
