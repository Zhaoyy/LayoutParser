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
  private static final String out = "src" + File.separator + "androidTest" + File.separator + "out";
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JRadioButton rbtnNormal;
  private JRadioButton rbtnButterKnife;
  private JLabel lblName;
  private JTextArea textArea1;
  private JRadioButton rbtnActivity;
  private JRadioButton rbtnView;
  private ButtonGroup typeGroup;
  private ButtonGroup rootGroup;

  private PsiFile mFile;

  private Project project;

  public ParserDialog() {
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    setTitle("LayoutParser");
    initView();
    textArea1.setEnabled(false);
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

  public static void main(String[] args) {
    ParserDialog dialog = new ParserDialog();
    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
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
    typeGroup.add(rbtnButterKnife);

    rootGroup = new ButtonGroup();
    rootGroup.add(rbtnActivity);
    rootGroup.add(rbtnView);

    if (Config.PARSER_TYPE == 0) {
      rbtnNormal.setSelected(true);
    } else {
      rbtnButterKnife.setSelected(true);
    }

    if (Config.ROOT_TYPE == 0) {
      rbtnActivity.setSelected(true);
      textArea1.setText(Config.PARSER_TYPE == 0 ? Samples.NORMAL_ACTIVITY : Samples.BUTTER_KNIFE);
    } else {
      rbtnView.setSelected(true);
      textArea1.setText(Config.PARSER_TYPE == 0 ? Samples.NORMAL_VIEW : Samples.BUTTER_KNIFE);
    }

    pack();

    rbtnNormal.addActionListener(this);

    rbtnButterKnife.addActionListener(this);
    rbtnActivity.addActionListener(this);
    rbtnView.addActionListener(this);
  }

  private File getOutFile() {
    String filePath = mFile.getVirtualFile().getPath();

    filePath = filePath.substring(0, filePath.indexOf("src")) + out + ".txt";

    File out = new File(filePath);

    return out;
  }

  private void onOK() {
    // add your code here

    XmlUtil xmlUtil = new XmlUtil();
    xmlUtil.setOutType(Config.PARSER_TYPE);
    List<String> result = xmlUtil.readXmlFile(new File(mFile.getVirtualFile().getPath()));
    //writeFile(result);
    printResult(result);
    buttonOK.setEnabled(false);
  }

  private void printResult(List<String> result) {
    StringBuilder sb = new StringBuilder();

    for (String s : result) {
      sb.append(s).append("\n");
    }

    textArea1.setText(sb.toString());
    textArea1.setEnabled(true);
    pack();
  }

  private void writeFile(List<String> result) {
    File out = getOutFile();

    try {
      FileWriter writer = new FileWriter(out, false);

      for (String s : result) {
        writer.write(s);
        writer.write("\n");
      }

      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }

    dispose();
  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

  @Override public void actionPerformed(ActionEvent e) {
    if (e.getSource() == rbtnNormal) {
      Config.PARSER_TYPE = 0;
    } else if (e.getSource() == rbtnButterKnife) {
      Config.PARSER_TYPE = 1;
    } else if (e.getSource() == rbtnActivity) {
      Config.ROOT_TYPE = 0;
    } else if (e.getSource() == rbtnView) {
      Config.ROOT_TYPE = 1;
    }

    if (Config.PARSER_TYPE == 1) {
      textArea1.setText(Samples.BUTTER_KNIFE);
    } else if (Config.ROOT_TYPE == 0) {
      textArea1.setText(Samples.NORMAL_ACTIVITY);
    } else {
      textArea1.setText(Samples.NORMAL_VIEW);
    }
    // change control state
    textArea1.setEnabled(false);
    buttonOK.setEnabled(true);
    pack();
  }
}
