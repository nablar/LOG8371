/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.ql.plan.logical;

import org.elasticsearch.xpack.ql.capabilities.Unresolvable;
import org.elasticsearch.xpack.ql.expression.Attribute;
import org.elasticsearch.xpack.ql.plan.TableIdentifier;
import org.elasticsearch.xpack.ql.tree.NodeInfo;
import org.elasticsearch.xpack.ql.tree.Source;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;

public class UnresolvedRelation extends LeafPlan implements Unresolvable {

    private final TableIdentifier table;
    private final boolean frozen;
    private final String alias;
    private final String unresolvedMsg;

    public UnresolvedRelation(Source source, TableIdentifier table, String alias, boolean frozen) {
        this(source, table, alias, frozen, null);
    }

    public UnresolvedRelation(Source source, TableIdentifier table, String alias, boolean frozen, String unresolvedMessage) {
        super(source);
        this.table = table;
        this.alias = alias;
        this.frozen = frozen;
        this.unresolvedMsg = unresolvedMessage == null ? "Unknown index [" + table.index() + "]" : unresolvedMessage;
    }

    @Override
    protected NodeInfo<UnresolvedRelation> info() {
        return NodeInfo.create(this, UnresolvedRelation::new, table, alias, frozen, unresolvedMsg);
    }

    public TableIdentifier table() {
        return table;
    }

    public String alias() {
        return alias;
    }

    public boolean frozen() {
        return frozen;
    }

    @Override
    public boolean resolved() {
        return false;
    }

    @Override
    public boolean expressionsResolved() {
        return false;
    }

    @Override
    public List<Attribute> output() {
        return Collections.emptyList();
    }

    @Override
    public String unresolvedMessage() {
        return unresolvedMsg;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source(), table, alias, unresolvedMsg);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        UnresolvedRelation other = (UnresolvedRelation) obj;
        return source().equals(other.source())
            && table.equals(other.table)
            && Objects.equals(alias, other.alias)
            && Objects.equals(frozen, other.frozen)
            && unresolvedMsg.equals(other.unresolvedMsg);
    }

    @Override
    public List<Object> nodeProperties() {
        return singletonList(table);
    }

    @Override
    public String toString() {
        return UNRESOLVED_PREFIX + table.index();
    }
}
