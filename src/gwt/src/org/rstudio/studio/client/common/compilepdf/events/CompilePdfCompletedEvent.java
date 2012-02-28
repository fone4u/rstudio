/*
 * CompilePdfCompletedEvent.java
 *
 * Copyright (C) 2009-11 by RStudio, Inc.
 *
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.studio.client.common.compilepdf.events;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CompilePdfCompletedEvent extends GwtEvent<CompilePdfCompletedEvent.Handler>
{  
   public static class Data extends JavaScriptObject
   {
      protected Data()
      {
      }
      
      public final native boolean getSucceeded() /*-{
         return this.succeeded;
      }-*/;
      
      public final native String getPdfUrl() /*-{
         return this.pdf_url;
      }-*/;
   }
   
   public interface Handler extends EventHandler
   {
      void onCompilePdfCompleted(CompilePdfCompletedEvent event);
   }

   public CompilePdfCompletedEvent(Data data)
   {
      data_ = data;
   }

   public boolean getSucceeded()
   {
      return data_.getSucceeded();
   }
    
   public String getPdfUrl()
   {
      return data_.getPdfUrl();
   }
   
   @Override
   public Type<Handler> getAssociatedType()
   {
      return TYPE;
   }

   @Override
   protected void dispatch(Handler handler)
   {
      handler.onCompilePdfCompleted(this);
   }
   
   private final Data data_;

   public static final Type<Handler> TYPE = new Type<Handler>();
}