package example.groovy

import example.entity.Category
import example.entity.Currency
import example.entity.Offer
import groovy.xml.XmlSlurper

class GroovyXmlParser {


    static Map<String, Set<String>> getStructure(String xmlUrl) {

        def shop = parseShop(xmlUrl)

        def result = [:].withDefault { new LinkedHashSet<String>() }

        shop.children().each { section ->

            def sectionName = section.name()

            if (!(sectionName in ['currencies', 'categories', 'offers'])) {
                return
            }

            section.children().each { item ->

                item.attributes().each { key, value ->
                    result[sectionName] << key.toString().toLowerCase()
                }

                item.children().each { child ->
                    if (child.name() != 'param') {
                        result[sectionName] << child.name().toLowerCase()
                    }
                }

                if (item.children().isEmpty() && item.text().trim()) {
                    result[sectionName] << 'value'
                }
            }
        }

        return result as Map<String, Set<String>>
    }

    static List<Currency> parseCurrencies(String xmlUrl) {
        def shop = parseShop(xmlUrl)
        def result = []

        shop.currencies.currency.each { currency ->
            result << new Currency(
                    id: currency.@id.text(),
                    rate: currency.@rate.toBigDecimal()
            )
        }

        return result
    }

    static List<Category> parseCategories(String xmlUrl) {
        def shop = parseShop(xmlUrl)
        def result = []

        shop.categories.category.each { category ->
            result << new Category(
                    id: category.@id.toInteger(),
                    parentId: category.@parentId?.text() ? category.@parentId.toInteger() : null,
                    value: category.text()
            )
        }

        return result
    }

    static List<Offer> parseOffers(String xmlUrl) {
        def shop = parseShop(xmlUrl)
        def result = []

        shop.offers.offer.each { offer ->
            result << new Offer(
                    id: offer.@id.toInteger(),
                    available: offer.@available.toBoolean(),
                    url: offer.url.text(),
                    price: offer.price.toBigDecimal(),
                    picture: offer.picture.text(),
                    name: offer.name.text(),
                    vendor: offer.vendor.text(),
                    description: offer.description.text(),
                    count: offer.count.toInteger(),
                    vendorCode: offer.vendorCode.text(),
                    categoryId: offer.categoryId.toInteger(),
                    currencyId: offer.currencyId.text()
            )
        }

        return result
    }


    private static def parseShop(String xmlUrl) {
        def xmlText = new URL(xmlUrl)
                .getText('UTF-8')
                .replaceAll(/<!DOCTYPE[^>]*>/, '')

        def root = new XmlSlurper().parseText(xmlText)
        return root.shop
    }
}
