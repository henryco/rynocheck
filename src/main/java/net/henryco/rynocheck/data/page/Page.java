package net.henryco.rynocheck.data.page;

public class Page implements DaoPage {

	private final long page;
	private final long pageSize;

	public Page(long page, long pageSize) {
		this.page = page;
		this.pageSize = pageSize;
	}

	@Override
	public long getPage() {
		return page;
	}

	@Override
	public long getPageSize() {
		return pageSize;
	}
}