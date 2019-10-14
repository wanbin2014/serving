package org.apache.spark.ml.tunning

import java.util

import org.apache.spark.ml.data.{SDFrame, SRow}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.transformer.ServingModel
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.apache.spark.ml.feature.utils.ModelUtils
import org.apache.spark.sql.types.StructType

class CrossValidatorServingModel(stage: CrossValidatorModel) extends ServingModel[CrossValidatorServingModel] {

  override def copy(extra: ParamMap): CrossValidatorServingModel = {
    new CrossValidatorServingModel(stage.copy(extra))
  }

  override def transform(dataset: SDFrame): SDFrame = {
    transformSchema(dataset.schema, logging = true)
    ModelUtils.transModel(stage.bestModel).transform(dataset)
  }

  override def transformSchema(schema: StructType): StructType = {
    stage.bestModel.transformSchema(schema)
  }

  override val uid: String = stage.uid

  override def prepareData(rows: Array[SRow]): SDFrame = {
    val model = ModelUtils.transModel(stage.bestModel)
    val data = model.prepareData(rows)
    this.setValueType(model.valueType())
    data
  }

  override def prepareData(feature: util.Map[String, _]): SDFrame = {
    val model = ModelUtils.transModel(stage.bestModel)
    val data = model.prepareData(feature)
    this.setValueType(model.valueType())
    data
  }
}

object CrossValidatorServingModel {
  def apply(stage: CrossValidatorModel): CrossValidatorServingModel = new CrossValidatorServingModel(stage)
}