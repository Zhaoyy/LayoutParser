import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import java.awt.Dimension;
import javax.swing.JOptionPane;

/**
 * Created by Administrator on 2015/7/7.
 */
public class MainAction extends AnAction {

  private Project project;  // current project
  private PsiFile mFile;    // the file you select

  private static final String FILE_TYPE_EOOR = "Sorry! It's not a layout file!\n"
      + "A layout file must be under the 'layout' directory, \nand a xml file.";

  public void actionPerformed(AnActionEvent e) {
    project = e.getProject();

    mFile = e.getData(PlatformDataKeys.PSI_FILE);

    System.out.println(project.getName());

    showDialog();

  }

  private void showDialog() {
    String filePath = mFile.getVirtualFile().getPath();

    if (!filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length()).equals("xml") ||
        !filePath.contains("layout")) {

      MsgDialog msgDialog = new MsgDialog(FILE_TYPE_EOOR);

      msgDialog.setMinimumSize(new Dimension(400, 200));

      msgDialog.setLocationRelativeTo(null);
      msgDialog.setVisible(true);
    } else {
      ParserDialog dialog = new ParserDialog();
      dialog.setmFile(mFile);
      dialog.setProject(project);

      dialog.setMinimumSize(new Dimension(400, 300));

      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);

    }

  }
}
