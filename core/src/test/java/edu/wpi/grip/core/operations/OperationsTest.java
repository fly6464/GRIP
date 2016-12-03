package edu.wpi.grip.core.operations;

import edu.wpi.grip.core.OperationMetaData;
import edu.wpi.grip.core.Step;
import edu.wpi.grip.core.metrics.MockTimer;
import edu.wpi.grip.core.operations.composite.BlobsReport;
import edu.wpi.grip.core.operations.composite.ContoursReport;
import edu.wpi.grip.core.operations.composite.LinesReport;
import edu.wpi.grip.core.operations.composite.RectsReport;
import edu.wpi.grip.core.util.MockExceptionWitness;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import org.bytedeco.javacpp.opencv_core;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.junit.Assert.assertTrue;
import static org.bytedeco.javacpp.opencv_imgproc.LineSegmentDetector;
@RunWith(Parameterized.class)
public class OperationsTest {

  @Parameter
  public OperationMetaData operationMetaData;

  @Parameters(name = "{index}: Operation({0})")
  public static Collection<Object[]> data() {
    EventBus eventBus = new EventBus();
    List<OperationMetaData> operationMetaDatas =
        ImmutableList.<OperationMetaData>builder()
            .addAll(
                OperationsFactory
                    .create(eventBus)
                    .operations())
            .addAll(
                OperationsFactory
                    .createCV(eventBus)
                    .operations())
            .build();

    Object[][] params = new Object[operationMetaDatas.size()][1];
    final int[] index = {0};
    operationMetaDatas.forEach(operationMeta -> {
      params[index[0]][0] = operationMeta;
      index[0]++;
    });
    return Arrays.asList(params);
  }


  @Test
  public void testCreateAllSteps() {
    final Step step =
        new Step.Factory((origin) -> new MockExceptionWitness(new EventBus(), origin),
            MockTimer.MOCK_FACTORY)
            .create(operationMetaData);
    step.setRemoved();
  }
  private boolean isMatEqual(Mat mat1, Mat mat2) {
    // treat two empty mat as identical as well
    if (mat1.empty() && mat2.empty()) {
      return true;
    }
    // if dimensionality of two mat is not identical, these two mat is not identical
    if (mat1.cols() != mat2.cols() || mat1.rows() != mat2.rows() || mat1.dims() != mat2.dims()) {
      return false;
    }
    Mat diff = new Mat();
    opencv_core.compare(mat1, mat2, diff, opencv_core.CMP_NE);
    int nz = opencv_core.countNonZero(diff);
    return nz == 0;
  }

  @Test
  public void testBolbsReportbyMingfei()
  {
    int[] sz = {256, 256};
    Mat mat= new Mat(2, sz, opencv_core.CV_8U, opencv_core.Scalar.all(1));
    List<BlobsReport.Blob> blobs= new ArrayList<>();
    BlobsReport blobsReport=new BlobsReport(mat,blobs);
    blobsReport.getX();
    blobsReport.getY();
    blobsReport.getBlobs();
    Mat out=blobsReport.getInput();
    blobsReport.getSize();
    blobsReport.toString();
    assertTrue(isMatEqual( out, mat));

  }
  @Test
  public void testLinesReportbyMingfei()
  {
    int[] sz = {256, 256};
    Mat mat= new Mat(2, sz, opencv_core.CV_8U, opencv_core.Scalar.all(1));
    List<LinesReport.Line> lines=new ArrayList<>();
    LineSegmentDetector lsd = new LineSegmentDetector();
    LinesReport linesReport=new LinesReport(lsd,mat,lines);
    linesReport.toString();
    Mat out=linesReport.getInput();
    linesReport.getAngle();
    linesReport.getLength();
    linesReport.getLines();
    linesReport.getX1();
    linesReport.getX2();
    linesReport.getY1();
    linesReport.getY2();
    assertTrue(isMatEqual( out, mat));
  }
  @Test
  public void testRectsReportbyMingfei()
  {
    int[] sz = {256, 256};
    Mat mat= new Mat(2, sz, opencv_core.CV_8U, opencv_core.Scalar.all(1));
    List<opencv_core.Rect> rectangles=new ArrayList<>();
    RectsReport rectsReport=new RectsReport(mat,rectangles);
    Mat out=rectsReport.getImage();
    rectsReport.getRectangles();
    rectsReport.height();
    rectsReport.topLeftX();
    rectsReport.topLeftY();
    rectsReport.width();
    assertTrue(isMatEqual( out, mat));
  }

}
