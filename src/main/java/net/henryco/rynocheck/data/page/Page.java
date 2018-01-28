package net.henryco.rynocheck.data.page;

public interface Page {

	long getPage();
	long getPageSize();

	default long getStartRow() {
		return getPage() * getPageSize();
	}

	abstract class factory {

		private factory() {}

		public static Page page(long page, long pageSize) {
			return new Page() {
				@Override
				public long getPage() {
					return page;
				}
				@Override
				public long getPageSize() {
					return pageSize;
				}
			};
		}

		public static Page ninePage(long page) {
			return page(page, 9);
		}

	}
}