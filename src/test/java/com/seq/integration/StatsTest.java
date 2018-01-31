package com.seq.integration;

import com.seq.TestUtils;
import com.seq.api.*;
import com.seq.http.*;

import org.junit.*;
import static org.junit.Assert.*;

public class StatsTest {
  @Test
  public void basicUsage() throws Exception {
    Client c = TestUtils.generateClient();
    Stats initial = Stats.get(c);

    Key k = new Key.Builder().create(c);

    Account acc = new Account.Builder().addKey(k).setQuorum(1).create(c);

    Asset asset = new Asset.Builder().addKey(k).setQuorum(1).create(c);

    new Transaction.Builder()
        .addAction(
            new Transaction.Builder.Action.Issue()
                .setAssetId(asset.id)
                .setAmount(1)
                .setDestinationAccountId(acc.id))
        .transact(c);

    Stats got = Stats.get(c);

    assertEquals(got.assetCount, initial.assetCount + 1);
    assertEquals(got.accountCount, initial.accountCount + 1);
    assertEquals(got.txCount, initial.txCount + 1);
  }
}
