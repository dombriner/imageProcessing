package imapro.filters;

import imapro.filters.assignment1.FlippingFilter;
import imapro.filters.assignment2.FixedColorIndexFilter;
import imapro.filters.assignment3.HistogramEqualizer;
import imapro.filters.assignment4.ConvolutionalMeanFilter;
import imapro.filters.assignment4.GradientFilter;
import imapro.filters.assignment4.LaplacianOfGaussianFilter;
import imapro.filters.assignment4.MaximumMinimumFilter;
import imapro.filters.assignment6.CompressionDecompressionFilter;
import imapro.filters.point.ContrastFilterFx;
import imapro.filters.project.filters.MosaiqueFilter;
import imapro.filters.project.filters.OilPaintingFilter;
import imapro.filters.project.filters.fxml.RippleFilter;
import sugarcube.imapro.ImaproFX;

public class LaunchMyFilters
{
    public static void main(String[] args)
    {
        ImaproFX.LaunchAll(ContrastFilterFx.LOADER,
//                FlippingFilter.LOADER,
//                FixedColorIndexFilter.LOADER,
//                HistogramEqualizer.LOADER,
//                ConvolutionalMeanFilter.LOADER,
//                LaplacianOfGaussianFilter.LOADER,
//                GradientFilter.LOADER,
//                MaximumMinimumFilter.LOADER,
//                CompressionDecompressionFilter.LOADER,
                OilPaintingFilter.LOADER,
                MosaiqueFilter.LOADER,
                RippleFilter.LOADER);
    }
}
