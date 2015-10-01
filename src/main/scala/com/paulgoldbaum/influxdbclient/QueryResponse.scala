package com.paulgoldbaum.influxdbclient

import spray.json._

class Record(namesIndex: Map[String, Int], values: List[Any]) {
  def apply(position: Int) = values(position)
  def apply(name: String) = values(namesIndex(name))
}

case class Series(name: String, columns: List[String], records: List[Record])

object QueryResponse {

  def fromJson(data: String) = {
    /*
    val root = data.parseJson.asInstanceOf[JsObject]
    val resultsArray = root.fields("results").asInstanceOf[JsArray]
    val resultObject = resultsArray.elements.head.asInstanceOf[JsObject]
    val seriesArray = resultObject.fields("series").asInstanceOf[JsArray]

    val series = seriesArray.elements.map(_.convertTo[Series]).toList
    new QueryResponse(series)
    */
  }

  def constructSeries(value: JsValue) = {
    val fields = value.asInstanceOf[JsObject].fields
    val seriesName = fields("name").asInstanceOf[JsString].value
    val columns = fields("columns").asInstanceOf[JsArray].elements.map {
      case JsString(column) => column
    }.toList

    val namesIndex = columns.zipWithIndex.toMap
    val records = fields("values").asInstanceOf[JsArray].elements.map(constructRecord(namesIndex, _)).toList
    new Series(seriesName, columns, records)
  }

  def constructRecord(namesIndex: Map[String, Int], value: JsValue) = {
    val valueArray = value.asInstanceOf[JsArray]
    val values = valueArray.elements.map {
      case JsNumber(num) => num
      case JsString(str) => str
      case JsBoolean(boolean) => boolean
    }.toList

    new Record(namesIndex, values)
  }
}

class QueryResponse(val series: List[Series]) {

}