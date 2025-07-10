package view.main.leftPanel.search;

import com.google.inject.Inject;
import di.BaseController;
import model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.SearchService;

import javax.swing.*;

public class SearchController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;
    private SearchPanel searchPanel;

    @Inject
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    protected void setupDependencies() {
        this.searchPanel = new SearchPanel();

        searchPanel.addSearchButtonListener(e -> {
            String searchText = searchPanel.getSearchText();

            if (searchText != null) {
                ChatResponse chatResponse = searchService.search(searchText);
                JOptionPane.showMessageDialog(searchPanel, "Search completed.");

                navigator.navigateTo("Chat", chatResponse);
            }
        });

    }

    @Override
    public void activate(Object... params) {

        if(searchPanel == null) {
            setupDependencies();
        }

    }

    @Override
    public void deactivate() {

    }
}
