package templates;

import edu.wpi.gripgenerator.templates.Enumeration;
import edu.wpi.gripgenerator.templates.SocketHintDeclaration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SocketHintDeclarationTest {
  SocketHintDeclaration testDeclaration = new SocketHintDeclaration("Mat", Arrays.asList("src1",
      "src2"), true);

  @Before
  public void setUp() throws Exception {

  }

  @Test
  @Ignore("No longer accurate")
  public void testGetDeclaration() {
    final String outputString = "private final SocketHint<Mat> src1OutputHint = new SocketHint.Builder(Mat.class).identifier(\"src1\").build(), src2OutputHint = new SocketHint.Builder(Mat.class).identifier(\"src2\").build();";
   // System.out.print(testDeclaration.getDeclaration().toString());
    assertEquals(outputString, testDeclaration.getDeclaration().toString());
  }
}
