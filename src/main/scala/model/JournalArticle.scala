package model

case class JournalArticle(title: String = "",
                          doi: String = "",
                          abstractText: String = "",
                          journal: Option[Journal] = None,
                          citation: Option[Citation] = None,
                          characteristics: List[Characteristic] = Nil,
                          authors: Option[List[Author]] = None,
                          affiliations: Option[List[Affiliation]] = None)

case class Journal(title: String, issn: String, eissn: String)

/* Not worrying about date for now*/
case class Citation(date: String, journalDate: String)

case class Author(name: String)

case class Affiliation(title: String)

case class Characteristic(source: String, text: String)
