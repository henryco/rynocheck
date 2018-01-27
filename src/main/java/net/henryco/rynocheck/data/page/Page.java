package net.henryco.rynocheck.data.page;

public class Page implements DaoPage {

	private final int page;
	private final int pageSize;

	public Page(int page, int pageSize) {
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