package imapro.filters;

import imapro.filters.point.ContrastFilterFx;
import imapro.filters.project.filters.MosaiqueFilter;
import imapro.filters.project.filters.OilPaintingFilter;
import imapro.filters.project.filters.RippleFilter;
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
