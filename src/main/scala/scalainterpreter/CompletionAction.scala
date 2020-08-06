
package  scalainterpreter


import java.awt.event.ActionEvent
import jsyntaxpane.SyntaxDocument
import javax.swing.text.JTextComponent
import jsyntaxpane.actions.gui.ComboCompletionDialog
import jsyntaxpane.actions.DefaultSyntaxAction
import scalaExec.Interpreter.GlobalValues
import tools.nsc.interpreter.shell.ReplCompletion
import scala.jdk.CollectionConverters 

//  the action triggered with the F7 key within the JSyntaxPane
class CompletionAction( completer: ReplCompletion )  {
  
   def complete(  ) {

     var posAutoCompletion =  -1
       
    // compute completion word (cw) and its start position (start) at the editing buffer
      val (cw, start) = {

      // if the user has selected text we return it 
      val sel = GlobalValues.editorPane.getSelectedText
         if( sel != null ) {
            (sel, GlobalValues.editorPane.getSelectionStart)
         } 
         
      else {  // the user has not selected text
         
       var pos = GlobalValues.editorPane.getCaretPosition-1  
       var doc = GlobalValues.editorPane.getDocument()
      
       
       var exited = false
        // take word part before cursor position
       var wb = ""
       var offset = pos
       while (offset >= 0 && exited==false) {
         var ch = doc.getText(offset, 1).charAt(0)
         if (ch == '.') {
               posAutoCompletion = offset-2   // replace the text after '.'
               if (posAutoCompletion<0) posAutoCompletion=0
          }

         var isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9') || ch=='.'  || ch=='_' || ch=='$'
         if (!isalphaNumeric)  exited=true
          else {
           wb = wb + ch
           offset -= 1
          }
          }
    
    if (posAutoCompletion ==  -1)  // a method name is not specified, thus set selection start to the beginning of the word
      posAutoCompletion  = offset+1
        
         var wordAtCursor = wb.reverse      
         
        //println("sterg: wordAtCursor = "+wordAtCursor+" posAutoCompletion = "+posAutoCompletion)
        
        //    (line.substring( 0, dot - start ), start)
          (wordAtCursor, posAutoCompletion)  
        
         }
      }
      

  
      val cwlen = cw.length()
      val m = completer.complete( cw, cwlen )
      
     //var dcl = new javax.swing.DefaultListModel  // the model for the completion list
    // var  clList = new javax.swing.JList(dcl)   // the completion's list
    
   //  GlobalValues.nameOfType = nameOfType  // keep class name in order to construct fully qualified names for accessing static members       
     // register our specialized list cell renderer that displays static members in bold
   //  clList.setCellRenderer(new javax.swing.FontCellRenderer())
     

      var completionList = new java.util.ArrayList[String]
      
    // nothing to complete
   if( m.candidates.isEmpty )  return
  else {
      val off = start + m.cursor
   //   target.select( off, start + cwlen )

    m.candidates match {
        // case one :: Nil =>
          //  System.out.println(one)
      case more =>
           more.foreach { 
            candidate =>   completionList.add(candidate.toString) 
           }
             
             }
             
           //  System.out.println("size of Completion List = "+completionList.size)
             
             scalaSciCommands.Inspect.displayCompletionList(cw, completionList)
             
           }
      
   }
}
