package org.rstudio.studio.client.htmlpreview.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;

import org.rstudio.core.client.dom.IFrameElementEx;
import org.rstudio.core.client.dom.WindowEx;
import org.rstudio.core.client.widget.Toolbar;
import org.rstudio.studio.client.htmlpreview.HTMLPreviewPresenter;

public class HTMLPreviewPanel extends ResizeComposite
                              implements HTMLPreviewPresenter.Display
{
   public HTMLPreviewPanel()
   {
      LayoutPanel panel = new LayoutPanel();
      
      Toolbar toolbar = new Toolbar();
      int tbHeight = toolbar.getHeight();
      panel.add(toolbar);
      panel.setWidgetLeftRight(toolbar, 0, Unit.PX, 0, Unit.PX);
      panel.setWidgetTopHeight(toolbar, 0, Unit.PX, tbHeight, Unit.PX);
      
      previewFrame_ = new PreviewFrame();
      previewFrame_.setSize("100%", "100%");
      panel.add(previewFrame_);
      panel.setWidgetLeftRight(previewFrame_,  0, Unit.PX, 0, Unit.PX);
      panel.setWidgetTopBottom(previewFrame_, tbHeight+1, Unit.PX, 0, Unit.PX);
      
      initWidget(panel);
   }
   
   @Override
   public void showProgress(String caption)
   {
      closeProgress();
      activeProgressDialog_ = new HTMLPreviewProgressDialog(caption);
   }
   
   @Override
   public void setProgressCaption(String caption)
   {
      activeProgressDialog_.setCaption(caption);
      
   }
   
   @Override
   public HandlerRegistration addProgressClickHandler(ClickHandler handler)
   {
      return activeProgressDialog_.addClickHandler(handler);
   }
   
   @Override
   public void showProgressOutput(String output)
   {
      activeProgressDialog_.showOutput(output);
   }

   @Override
   public void stopProgress()
   {
      activeProgressDialog_.stopProgress();
   }
   
   @Override
   public void closeProgress()
   {
      if (activeProgressDialog_ != null)
      {
         activeProgressDialog_.dismiss();
         activeProgressDialog_ = null;
      }
   }
   
   @Override
   public void showPreview(String url, boolean enableScripts)
   {
      previewFrame_.setScriptsEnabled(enableScripts);
      previewFrame_.navigate(url);
   }
   
   private class PreviewFrame extends Frame
   {
      public PreviewFrame()
      {
         setStylePrimaryName("rstudio-HelpFrame");
         setScriptsEnabled(false);
      }
      
      public void setScriptsEnabled(boolean scriptsEnabled)
      {
         // enable scripts for the iframe sandbox if requested. note that
         // if we do allow scripts we need to make sure that same-origin
         // is not allowed (so the scripts are confined to this frame). 
         // however if scripts are not allowed we explicitly allow same-origin
         // so that reloading will preseve scroll position. 
         if (scriptsEnabled)
            getElement().setAttribute("sandbox", "allow-scripts");
         else
            getElement().setAttribute("sandbox", "allow-same-origin");
      }
      
      public void navigate(final String url)
      {
         RepeatingCommand navigateCommand = new RepeatingCommand() {
            @Override
            public boolean execute()
            {
               if (getIFrame() != null && getWindow() != null)
               {
                  if (url.equals(getWindow().getLocationHref()))
                  {
                     getWindow().reload();
                  }
                  else
                  {
                     getWindow().replaceLocationHref(url);
                  }
                  return false;
               }
               else
               {
                  return true;
               }
            }
         };

         if (navigateCommand.execute())
            Scheduler.get().scheduleFixedDelay(navigateCommand, 50);      
      }
      
      private IFrameElementEx getIFrame()
      {
         return getElement().cast();
      }
      
      protected WindowEx getWindow()
      {
         return getIFrame().getContentWindow();
      }

   }
 
   private final PreviewFrame previewFrame_;
   private HTMLPreviewProgressDialog activeProgressDialog_;
}
