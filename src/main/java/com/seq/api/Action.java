package com.seq.api;

import com.seq.exception.APIException;
import com.seq.exception.BadURLException;
import com.seq.exception.ChainException;
import com.seq.http.Client;

import com.seq.exception.ConnectivityException;
import com.seq.exception.JSONException;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Action queries are designed to provide insights into those actions.
 * There are two types of queries you can run against them; one is "ListBuilder",
 * one is "SumBuilder". ListBuilder simply returns a list of Action objects that match
 * the filter; SumBuilder sums over the amount fields based on the filter and
 * the groupBy param and returns ActionSum objects.
 *
 * Please refer to ActionSum class for more details.
 */
public class Action {
  /**
   * The number of units of the action's flavor
   */
  public long amount;

  /**
   * The type of the action.
   * Currently, there are three options: "issue", "transfer", "retire".
   */
  public String type;

  /**
   * A unique ID.
   */
  public String id;

  /**
   * The ID of the transaction in which the action appears.
   */
  @SerializedName("transaction_id")
  public String transactionId;

  /**
   * Time of the action.
   */
  public Date timestamp;

  /**
   * The ID of the flavor held by the action.
   */
  @SerializedName("flavor_id")
  public String flavorId;

  /**
   * The tags of the flavor held by the action.
   */
  @SerializedName("flavor_tags")
  public Map<String, Object> flavorTags;

  /**
   * The ID of the asset held by the action.
   * @deprecated use {@link #flavorId} instead
   */
  @Deprecated
  @SerializedName("asset_id")
  public String assetId;

  /**
   * The alias of the asset held by the action.
   * @deprecated use {@link #flavorId} instead
   */
  @Deprecated
  @SerializedName("asset_alias")
  public String assetAlias;

  /**
   * The tags of the asset held by the action.
   * @deprecated use {@link #flavorTags} instead
   */
  @Deprecated
  @SerializedName("asset_tags")
  public Map<String, Object> assetTags;

  /**
   * The ID of the source account executing the action.
   */
  @SerializedName("source_account_id")
  public String sourceAccountId;

  /**
   * The alias of the source account executing the action.
   * @deprecated see {@link #sourceAccountId} instead
   */
  @Deprecated
  @SerializedName("source_account_alias")
  public String sourceAccountAlias;

  /**
   * The tags of the source account executing the action.
   */
  @SerializedName("source_account_tags")
  public Map<String, Object> sourceAccountTags;

  /**
   * The ID of the destination account affected by the action.
   */
  @SerializedName("destination_account_id")
  public String destinationAccountId;

  /**
   * The alias of the destination account affected by the action.
   * @deprecated see {@link #destinationAccountId} instead
   */
  @Deprecated
  @SerializedName("destination_account_alias")
  public String destinationAccountAlias;

  /**
   * The tags of the destination account affected by the action.
   */
  @SerializedName("destination_account_tags")
  public Map<String, Object> destinationAccountTags;

  /**
   * User-specified key-value data embedded in the action.
   */
  @SerializedName("reference_data")
  public Object referenceData;

  /**
   * A single page of actions returned from a query.
   */
  public static class Page extends BasePage<Action> {}

  /**
   * Iterable interface for consuming individual actions from a query.
   */
  public static class ItemIterable extends BaseItemIterable<Action> {
    public ItemIterable(Client client, String path, Query nextQuery) {
      super(client, path, nextQuery, Page.class);
    }
  }

  /**
   * Iterable interface for consuming pages of actions from a query.
   */
  public static class PageIterable extends BasePageIterable<Page> {
    public PageIterable(Client client, String path, Query nextQuery) {
      super(client, path, nextQuery, Page.class);
    }
  }

  /**
   * A builder class for querying actions in the ledger.
   *
   * <p>List all actions after a certain time:</p>
   * <pre>{@code
   * Action.ItemIterable actions = new Action.ListBuilder()
   *   .setFilter("timestamp > $1")
   *   .addFilterParameter("1985-10-26T01:21:00Z")
   *   .getIterable(ledger);
   * for (Action action : actions) {
   *   System.out.println("timestamp: " + action.timestamp);
   *   System.out.println("amount: " + action.amount);
   * }
   * }</pre>
   */
  public static class ListBuilder extends BaseQueryBuilder<ListBuilder> {
    /**
     * Executes the query, returning a page of actions that match the query.
     * @param client ledger API connection object
     * @return a page of actions
     * @throws ChainException
     */
    public Page getPage(Client client) throws ChainException {
      return client.request("list-actions", this.next, Page.class);
    }

    /**
     * Executes the query, returning an iterable over actions that match the
     * query.
     * @param client ledger API connection object
     * @return an iterable over actions
     * @throws ChainException
     */
    public ItemIterable getIterable(Client client) throws ChainException {
      return new ItemIterable(client, "list-actions", this.next);
    }

    /**
     * Executes the query, returning an iterable over pages of actions that
     * match the query.
     * @param client ledger API connection object
     * @return an iterable over pages of actions
     * @throws ChainException
     */
    public PageIterable getPageIterable(Client client) throws ChainException {
      return new PageIterable(client, "list-actions", this.next);
    }
  }

  /**
   * A builder class for querying actionsums in the ledger.
   */
  public static class SumBuilder extends BaseQueryBuilder<SumBuilder> {
    /**
     * Executes the query, returning a page of actionsums that match the query.
     * @param client ledger API connection object
     * @return a page of actionsums
     * @throws ChainException
     */
    public ActionSum.Page getPage(Client client) throws ChainException {
      return client.request("sum-actions", this.next, ActionSum.Page.class);
    }

    /**
     * Executes the query, returning an iterable over actionsums that match the
     * query.
     * @param client ledger API connection object
     * @return an iterable over actionsums
     * @throws ChainException
     */
    public ActionSum.ItemIterable getIterable(Client client) throws ChainException {
      return new ActionSum.ItemIterable(client, "sum-actions", this.next);
    }

    /**
     * Executes the query, returning an iterable over pages of actionsums that
     * match the query.
     * @param client ledger API connection object
     * @return an iterable over pages of actionsums
     * @throws ChainException
     */
    public ActionSum.PageIterable getPageIterable(Client client) throws ChainException {
      return new ActionSum.PageIterable(client, "sum-actions", this.next);
    }

    /**
     * Specifies the fields along which action values will be summed.
     * @param groupBy a list of action fields
     * @return updated builder
     */
    public SumBuilder setGroupBy(List<String> groupBy) {
      this.next.groupBy = new ArrayList<>(groupBy);
      return this;
    }

    /**
     * Adds a field along which action values will be summed.
     * @param field name of a action field
     * @return updated builder
     */
    public SumBuilder addGroupByField(String field) {
      if (this.next.groupBy == null) {
        this.next.groupBy = new ArrayList<>();
      }
      this.next.groupBy.add(field);
      return this;
    }
  }
}
