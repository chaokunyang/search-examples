package com.timeyang.search.hibernate.example;

// /**
//  * @author chaokunyang
//  */
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Entity
// @Indexed
// @Builder
// public class Book {
//
//     @Id
//     @GeneratedValue
//     private Integer id;
//
//     @Field(index=Index.YES, analyze= Analyze.YES, store=Store.NO)
//     private String title;
//
//     @Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
//     private String subtitle;
//
//     @Field(index=Index.YES, analyze=Analyze.NO, store=Store.YES)
//     @DateBridge(resolution=Resolution.DAY)
//     private Date publicationDate;
//
//     @IndexedEmbedded
//     @ManyToMany
//     private Set<Author> authors;
//
// }

import lombok.*;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.Set;

/**
 * @author chaokunyang
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Indexed
@AnalyzerDef(name = "customAnalyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                        @Parameter(name = "language", value = "English")
                })
        })
@Builder
public class Book {

    @Id
    @GeneratedValue
    @DocumentId
    private Integer id;

    @Field(index=Index.YES, analyze= Analyze.YES, store= Store.NO)
    @Analyzer(definition = "customAnalyzer")
    private String title;

    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
    @Analyzer(definition = "customAnalyzer")
    private String subtitle;

    @Field(index=Index.YES, analyze=Analyze.NO, store=Store.YES)
    @DateBridge(resolution= Resolution.DAY)
    private Date publicationDate;

    @IndexedEmbedded
    @ManyToMany
    private Set<Author> authors;

}
