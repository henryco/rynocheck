package net.henryco.rynocheck.data.page;

public interface DaoPage {

	long getPage();
	long getPageSize();

	default long getStartRow() {
		return getPage() * getPageSize();
	}
}