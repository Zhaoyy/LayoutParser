import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import config.Config;
import config.Samples;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class ParserDialog extends JDialog implements ActionListener {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JRadioButton rbtnNormal;
  private JRadioButton rbtnAA;
  private JLabel lblName;
  private JLabel lblOut;
  private JTextArea textArea1;
  private JRadioButton rbtnActivity;
  private JRadioButton rbtnView;

  private static final String out =
      "src" + File.separator + "androidTest" + File.separator + "out.txt";

  private ButtonGroup typeGroup;
  private ButtonGroup rootGroup;

  private PsiFile mFile;

  private Project project;

  public ParserDialog() {
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    initView();

    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

    buttonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    });

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                           onCancel();
                                         }
                                       }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  public void setmFile(PsiFile mFile) {
    this.mFile = mFile;
    lblName.setText(mFile.getName());
  }

  public void setProject(Project project) {
    this.project = project;
  }

  private void initView() {

    typeGroup = new ButtonGroup();
    typeGroup.add(rbtnNormal);
    typeGroup.add(rbtnAA);

    rootGroup = new ButtonGroup();
    rootGroup.add(rbtnActivity);
    rootGroup.add(rbtnView);

    textArea1.setEnabled(false);

    if (Config.PARSER_TYPE == 0) {
      rbtnNormal.setSelected(true);
      textArea1.setText(Samples.NORMAL_ACTIVITY);
    } else {
      rbtnAA.setSelected(true);
      textArea1.setText(Samples.AA);
    }

    rbtnNormal.addActionListener(this);

    rbtnAA.addActionListener(this);
    rbtnActivity.addActionListener(this);
    rbtnView.addActionListener(this);
  }

  private String getOutFile() {
    String filePath = mFile.getVirtualFile().getPath();
    System.out.println(filePath.substring(0, filePath.indexOf("src")));

    return filePath.substring(0, filePath.indexOf("src")) + out;
  }

  private void onOK() {
    // add your code here

    XmlUtil xmlUtil = new XmlUtil();
    xmlUtil.setOutType(Config.PARSER_TYPE);
    List<String> result = xmlUtil.readXmlFile(new File(mFile.getVirtualFile().getPath()));
    writeFile(result);
    dispose();
  }

  private void writeFile(List<String> result) {
    File out = new File(getOutFile());
    if (!out.getParentFile().exists()) {
      out.getParentFile().mkdirs();
    }
    try {
      FileWriter writer = new FileWriter(out, false);

      for (String s : result) {
        writer.write(s);
        writer.write("\n");
      }

      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

  public static void main(String[] args) {
    ParserDialog dialog = new ParserDialog();
    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
  }

  @Override public void actionPerformed(ActionEvent e) {
    if (e.getSource() == rbtnNormal) {
      Config.PARSER_TYPE = 0;
    } else if (e.getSource() == rbtnAA) {
      Config.PARSER_TYPE = 1;
    } else if (e.getSource() == rbtnActivity) {
      Config.ROOT_TYPE = 0;
    } else if (e.getSource() == rbtnView) {
      Config.ROOT_TYPE = 1;
    }

    if (Config.PARSER_TYPE == 1) {
      textArea1.setText(Samples.AA);
    } else if (Config.ROOT_TYPE == 0){
      textArea1.setText(Samples.NORMAL_ACTIVITY);
    } else {
      textArea1.setText(Samples.NORMAL_VIEW);
    }

  }
}
