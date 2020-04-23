 
 package scalaExec.scalaLab;
 
 
 import javax.swing.*;
 import java.awt.event.*;
 import scalaExec.Interpreter.GlobalValues;
 import static scalaExec.Interpreter.GlobalValues.*;
 
 
public class progPatterns {

   JMenuBar ftargetMenuBar;
   
  public progPatterns(JMenuBar targetMenuBar) {
    ftargetMenuBar = targetMenuBar;
    }
    
  public void constructProgPatternsMenuBar() {
    JMenu  progPatternsMenu = new JMenu("Common Patterns");
    progPatternsMenu.setFont(GlobalValues.uifont);
    
    JMenuItem  sourceFromFileJMenuItem = new JMenuItem("sourceFromFile");
    sourceFromFileJMenuItem.setFont(GlobalValues.uifont);
    
    sourceFromFileJMenuItem.addActionListener((ActionEvent e) -> {
     GlobalValues.editorPane.setText(
       GlobalValues.editorPane.getText()+"\n Source.fromFile() \n");
      });
    progPatternsMenu.add(sourceFromFileJMenuItem);
    
    ftargetMenuBar.add(progPatternsMenu);
    }
 }
