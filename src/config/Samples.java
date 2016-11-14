package config;

/**
 * Samples
 *
 * @author Mislead
 *         DATE: 2015/7/7
 *         DESC:
 **/
public class Samples {

  public static final String NORMAL_ACTIVITY = "\n\tprivate TextView textView;"
      + "\n"
      + "\ttextView = (TextView) findViewById(R.id.textView);";
  public static final String NORMAL_VIEW = "\n\tprivate TextView textView;"
      + "\n"
      + "\ttextView = (TextView) view.findViewById(R.id.textView);";
  public static final String AA = "\n\t@ViewById\n\tTextView textView";

  public static final String BUTTER_KNIFE = "\n\t@BindView(R.id.textView) TextView textView;";
  private static String TAG = "Samples";
}
