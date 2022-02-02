package io.micronaut.guides

import java.util.regex.Pattern

class ThemeProcessor {

    private static final Pattern TOC_REGEX = ~/(?s)<div id="toc" class="toc2">\s*<div id="toctitle">Table of Contents<\/div>(.*)<\/div>\s*<\/div>\s*<div id="content">/
    private static final Pattern CONTENT_REGEX = ~/(?s)<div class="sectionbody">(.*)<\/div>\s*<\/div>\s*<\/div>\s*<\/body>/

    static void applyThemes(File template, File dist,
                            File guidesFolder, String metadataConfigName) {

        String templateText = template.text
        List<GuideMetadata> metadatas = GuideProjectGenerator.parseGuidesMetadata(guidesFolder, metadataConfigName)

        for (GuideMetadata metadata : metadatas) {
            if (!Utils.process(metadata, false)) {
                continue
            }

            List<GuidesOption> guidesOptionList = GuideProjectGenerator.guidesOptions(metadata)
            for (GuidesOption guidesOption : guidesOptionList) {

                String text = templateText
                String folder = GuideProjectGenerator.folderName(metadata.slug, guidesOption)
                try {
                    File output = new File(dist.path, folder + ".html")
                    String html = output.text

                    String toc = html.find(TOC_REGEX){match, table -> table } ?: ''

                    String content =  html.find(CONTENT_REGEX){match, content -> content } ?: ''

                    content = content == '' ? '' : '''\
<div class="sect1">
<div class="sectionbody">
''' + content

                    String breadcrumb = '<a href="' + metadata.slug + '.html">' + metadata.title + '</a>' +
                            ' » <span class="breadcrumb_last" aria-current="page">' + guidesOption.buildTool + ' | ' + guidesOption.language + '</span>'

                    text = text.replace("@title@", metadata.title)
                    text = text.replace("@twittercard@", IndexGenerator.twitterCardHtml(dist, metadata))
                    text = text.replace("@breadcrumb@", breadcrumb)
                    text = text.replace("@toctitle@", 'Table of Contents')
                    text = text.replace("@bodyclass@", 'guide')
                    text = text.replace("@toccontent@", toc)
                    text = text.replace("@content@", content)
                    output.text = text
                } catch (FileNotFoundException ignored) {
                }
            }
        }
    }
}
