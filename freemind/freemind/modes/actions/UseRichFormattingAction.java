/*
 * FreeMind - a program for creating and viewing mind maps
 * Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Created on 26.01.2006.
 */
/* $Id: UseRichFormattingAction.java,v 1.1.4.3 2006-05-30 21:36:17 christianfoltin Exp $ */
package freemind.modes.actions;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import freemind.modes.ControllerAdapter;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.main.HtmlTools;
import freemind.view.mindmapview.NodeView;

public class UseRichFormattingAction extends AbstractAction {
   private final ControllerAdapter controller;
   public UseRichFormattingAction(ControllerAdapter controller) {
      super(controller.getText("use_rich_formatting"));
      // new ImageIcon(controller.getResource("images/whatnot.png")));
      this.controller = controller; }
   public boolean isEnabled() {
      if (1 == 0) {
         // Dan: The following code probably needs that the node context menu gets somehow refreshed
         // whenever it pops. But I do not know how to ensure that.
         System.err.println("IS-ENABLED");
         for (ListIterator it = controller.getView().getSelecteds().listIterator();it.hasNext();) {
            NodeView selected = (NodeView)it.next();
            String nodeText = selected.getModel().toString();
            if (!nodeText.startsWith("<html>")) {
               return true; }}
         return false; }
      return true; }
   public void actionPerformed(ActionEvent e) {
      if (controller.getMap() == null) {
         return; }
      controller.getController().setProperty("use_rich_text_in_new_long_nodes","true");
      for(ListIterator it = controller.getView().getSelecteds().listIterator();it.hasNext();) {
         NodeView selected = (NodeView)it.next();
         String nodeText = selected.getModel().toString();
         if (!nodeText.startsWith("<html>")) {
            ((MindMapMapModel)controller.getModel()).
               changeNode(selected.getModel(), HtmlTools.plainToHTML(nodeText)); }}}}