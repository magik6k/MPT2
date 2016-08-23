package net.magik6k.mpt.client.menu

import net.magik6k.mpt.client.menu.util.{TreeNodeButton, TreeEntry, TreeButton, TreeMenu}
import net.magik6k.mpt.client.profile.{Profile, ProfileTab}
import net.magik6k.mpt.client.repositories.{FileTab, PackageTab, RepositoryTab, RepositoriesTab}
import net.magik6k.mpt.client.util.{ResourceManager, icon}
import net.magik6k.mpt.client.util.tags.Tags.span
import org.scalajs.dom.document
import org.scalajs.dom.window

import scala.scalajs.js.JSON
import scala.scalajs.js
import scala.collection.mutable

object Menu {
  val reposNode = TreeNodeButton("Repositories", e => RepositoriesTab.focus())
  var repoMap: Map[String, (mutable.Map[String, (mutable.Map[String, TreeNodeButton], TreeNodeButton)], TreeNodeButton)] = null

  def createMenu() = {
    val menu = new TreeMenu(
      TreeButton(span(icon("left224"), "Logout"), e => window.location.href = "/logout"),
      TreeButton(span(icon("users81"), "Profile"), e => ProfileTab.focus()),
      TreeEntry(span(icon("magnifying47"), "Search Package")),
      reposNode
    )

    document.getElementById("side-menu").appendChild(menu.tag)

    reposNode.firstOpen(() => {
      ResourceManager.get("/api/v1/repository/of/" + Profile.name, data => {
        repoMap = Map.empty[String, (mutable.Map[String, (mutable.Map[String, TreeNodeButton], TreeNodeButton)], TreeNodeButton)]
        val raw = JSON.parse(data)
        val repos: mutable.Seq[String] = raw.asInstanceOf[js.Array[String]]
        repos.foreach(addRepo)
      })
    })
  }

  def addRepo(r: String): Unit = {
    if(repoMap == null) return
    val rbutton = TreeNodeButton(span(r), e => RepositoryTab.focus(r))
    reposNode.add(rbutton)
    rbutton.firstOpen(() => {
      ResourceManager.get("/api/v1/packages/in/" + r, data => {
        repoMap += r -> ((mutable.Map.empty[String, (mutable.Map[String, TreeNodeButton], TreeNodeButton)], rbutton))
        val raw = JSON.parse(data)
        val packages: mutable.Seq[String] = raw.asInstanceOf[js.Array[String]]
        packages.foreach(p => addPackage(r, p))
      })
    })
  }

  def addPackage(repo: String, name: String) = {
    repoMap.get(repo) match {
      case Some((pmap, rbutton)) =>
        val pbutton = new TreeNodeButton(name, e => PackageTab.focus(name, repo))
        rbutton.add(pbutton)
        pbutton.firstOpen(() => {
          ResourceManager.get("/api/v1/package/" + name + "/files", data => {
            pmap += name -> ((mutable.Map.empty[String, TreeNodeButton], pbutton))
            val raw = JSON.parse(data)
            val files: mutable.Seq[String] = raw.asInstanceOf[js.Array[String]]
            //files.foreach(f => pbutton.add(TreeButton(f, e=>{})))
            files.foreach(f => addFile(repo, name, f))
          })
        })
      case _ =>
    }
  }

  def addFile(repo: String, pack: String, file: String): Unit = {
    val fbutton = new TreeButton(file, e => FileTab.focus(repo, pack, file))

    repoMap.get(repo) match {
      case Some((pmap, rbutton)) =>
        pmap.get(pack) match {
          case Some((fmap, pbutton)) =>
            pbutton.add(fbutton)
          case _ =>
        }
      case _ =>
    }
  }
}
