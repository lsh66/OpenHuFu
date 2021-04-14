package tk.onedb.core.sql.rel;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.type.RelDataType;

import tk.onedb.core.data.Header;
import tk.onedb.core.sql.expression.OneDBReference;
import tk.onedb.core.sql.rule.OneDBRules;

public class OneDBTableScan extends TableScan implements OneDBRel {
  final OneDBTable oneDBTable;
  final RelDataType projectRowType;

  public OneDBTableScan(RelOptCluster cluster, RelTraitSet traitSet, RelOptTable table, OneDBTable oneDBTable, RelDataType projectRowType) {
    super(cluster, traitSet, ImmutableList.of(), table);
    this.oneDBTable = oneDBTable;
    this.projectRowType = projectRowType;
  }

  public Header getHeader() {
    return oneDBTable.getHeader();
  }

  @Override
  public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
    return this;
  }

  @Override public RelDataType deriveRowType() {
    return projectRowType != null ? projectRowType : super.deriveRowType();
  }

  @Override public void register(RelOptPlanner planner) {
    planner.addRule(OneDBRules.TO_ENUMERABLE);
    for (RelOptRule rule : OneDBRules.RULES) {
      planner.addRule(rule);
    }
  }

  @Override
  public void implement(Implementor implementor) {
    implementor.setOneDBTable(oneDBTable);
    implementor.setTable(table);
    implementor.setSelectExps(OneDBReference.fromHeader(oneDBTable.getHeader()));
  }
}