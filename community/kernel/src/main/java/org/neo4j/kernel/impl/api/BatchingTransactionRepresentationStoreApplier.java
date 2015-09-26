/*
 * Copyright (c) 2002-2015 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.api;

import org.neo4j.helpers.Provider;
import org.neo4j.kernel.KernelHealth;
import org.neo4j.kernel.api.labelscan.LabelScanStore;
import org.neo4j.kernel.impl.api.index.IndexingService;
import org.neo4j.kernel.impl.core.CacheAccessBackDoor;
import org.neo4j.kernel.impl.index.IndexConfigStore;
import org.neo4j.kernel.impl.locking.LockService;
import org.neo4j.kernel.impl.store.NeoStores;
import org.neo4j.kernel.impl.util.IdOrderingQueue;
import org.neo4j.unsafe.batchinsert.LabelScanWriter;

/**
 * {@link TransactionRepresentationStoreApplier} that builds services made for batching transactions.
 * Transaction data can be cached and applied as one batch when a threshold is reached, so ensuring that transaction
 * data is actually written will have to be done by calling {@link #closeBatch()}.
 */
public class BatchingTransactionRepresentationStoreApplier extends TransactionRepresentationStoreApplier
{
    public BatchingTransactionRepresentationStoreApplier( IndexingService indexingService,
            LabelScanStore labelScanStore, NeoStores neoStore, CacheAccessBackDoor cacheAccess,
            LockService lockService, LegacyIndexApplierLookup legacyIndexApplierLookup,
            IndexConfigStore indexConfigStore, KernelHealth kernelHealth,
            IdOrderingQueue legacyIndexTransactionOrdering )
    {
        super( indexingService, alwaysCreateNewWriter( labelScanStore ),
                neoStore, cacheAccess, lockService,
                legacyIndexApplierLookup,
                indexConfigStore, kernelHealth, legacyIndexTransactionOrdering );
    }

    private static Provider<LabelScanWriter> alwaysCreateNewWriter( final LabelScanStore labelScanStore )
    {
        return new Provider<LabelScanWriter>()
        {
            @Override
            public LabelScanWriter instance()
            {
                return labelScanStore.newWriter();
            }
        };
    }
}
