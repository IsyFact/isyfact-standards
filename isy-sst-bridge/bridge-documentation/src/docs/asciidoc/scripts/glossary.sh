#!/bin/bash

# Aus Dokumentvorlagen 2.0 übernommen, mit geänderter "allDocDirCmd"

# Erzeugt für Dokumente ein individuellen Glossar
#
# Funktionsweise:
# - common/glossary.adoc enthält alle Glossarbegriffe in der Form [id="GlossarBegriff", ... ].
#   Baue eine Liste aller Begriffe (1)
# - Suche in adoc-Dateien aller Dokumente (2) nach Referenzen der Form <<GlossarBegriff>> (3)
# - Extrahiere für jeden gefundenen Begriff den Eintrag aus glossary.adoc und übernehme in individuelle
#   glossary.adoc des Dokuments (4)
# - nach dynamischen rekursiven Bau des Glossars die zu tiefen (noch nicht aufgelösten Glossar-Links aus
#   dem erzeugten Glossar säubern (5)

IFS=$'\n'

# (1) suche die zu referenzierenden Glossar-Begriffe aus dem der Master-Glossar-Datei in isy-dokumentatvorlage
readGlossaryTerms() {
    gawk 'match($0, /\[id="(.+)",.+\]/, m) { print m[1] }' $curDir/common/glossary.adoc | grep 'glossar-' | grep -v 'image-glossar-' | grep -v 'glossar-YYY-ZZZ'
}

# suche in jeder Zeile nach spitzen Klammern (und glossar) und spitze Klammer zu. Schreibe sie raus. Verwerfe Doppelte.

# (3a)
findTerms() {

    if [ -f $dir/grabbedTerms.txt ];
    then
      rm $dir/grabbedTerms.txt
    fi
    touch $dir/grabbedTerms.txt
    cat  $dir/thisdoc.adoc $dir/inhalt.adoc $dir/anhaenge.adoc > $dir/TermSrc-temp.adoc
    for term in $@
    do
        cat $dir/TermSrc-temp.adoc | gawk '{while(match($0,/<<([^<>]+)>>/)) {print substr($0,RSTART+2,RLENGTH-4); $0=substr($0,RSTART+RLENGTH)}}' | grep $term  | grep -v 'image-glossar-' | sort -u >> $dir/grabbedTerms.txt
        # cat $dir/thisdoc.adoc $dir/inhalt.adoc $dir/anhaenge.adoc | gawk -v foundref=$term 'match($0, /<<([^<>]+)>>/, m) && m[1] == foundref { print m[1]; }' | grep 'glossar-' | grep -v 'image-glossar-' | sort -u
    done

    touch $dir/grabbedTerms-temp.txt
    cat $dir/grabbedTerms.txt | sort -u > $dir/grabbedTerms-temp.txt
    cat $dir/grabbedTerms-temp.txt > $dir/grabbedTerms.txt

    cat $dir/grabbedTerms.txt

    if [ -f $dir/grabbedTerms-temp.txt ];
    then
      rm $dir/grabbedTerms-temp.txt
    fi
    if [ -f $dir/grabbedTerms.txt ];
    then
      rm $dir/grabbedTerms.txt
    fi

    rm $dir/TermSrc-temp.adoc
}

# (3b)
findTermswithGlos() {

    if [ -f $dir/grabbedTerms.txt ];
    then
      rm $dir/grabbedTerms.txt
    fi
    touch $dir/grabbedTerms.txt
    cat  $dir/thisdoc.adoc $dir/inhalt.adoc $dir/anhaenge.adoc $dir/anhaenge.adoc $dir/glossary.adoc > $dir/TermSrc-temp.adoc


    for term in $@
    do
        cat $dir/TermSrc-temp.adoc | gawk '{while(match($0,/<<([^<>]+)>>/)) {print substr($0,RSTART+2,RLENGTH-4); $0=substr($0,RSTART+RLENGTH)}}' | grep $term  | grep -v 'image-glossar-' | sort -u >> $dir/grabbedTerms.txt
    done

    touch $dir/grabbedTerms-temp.txt
    cat $dir/grabbedTerms.txt | sort -u > $dir/grabbedTerms-temp.txt
    cat $dir/grabbedTerms-temp.txt > $dir/grabbedTerms.txt

    cat $dir/grabbedTerms.txt

    if [ -f $dir/grabbedTerms-temp.txt ];
    then
      rm $dir/grabbedTerms-temp.txt
    fi
    if [ -f $dir/grabbedTerms.txt ];
    then
      rm $dir/grabbedTerms.txt
    fi
    rm $dir/TermSrc-temp.adoc
}

# (4)
buildDocumentGlossary() {

    glossfil=$curDir/common/glossary.adoc
    glossfileTmp=$curDir/common/glossary-temp.adoc

    cat $glossfil | sed '$!N;s/\[id="glossar-/\n\[id="glossar-/g' > $glossfileTmp

    terms=($@)
    termsSize=${#terms[@]}

    if [ $termsSize -gt 0 ]
    then
        echo -e "\n\n[glossary]\n== Glossar\n" > $dir/glossary.adoc

        echo -e "\n\n:imagesdir: "$ArgOneDir/10_IsyFact-Standards/00_Allgemein/IsyFact-Glossar/images"\n" >> $dir/glossary.adoc

        term="glossar-Abbildungsbeschreibungen"
        searchterm="id=\"$term\""
        gawk -v foundterm=$term -v searchterm=$searchterm 'BEGIN {RS="\n\n\n";FS=""} match($0, /\[id="(.+)",.+\]/, m)  { if ( index($0,searchterm) >0 ) { print $0 } }' $glossfileTmp | grep -v "$searchterm" >> $dir/glossary.adoc
        echo -e "\n\n" >> $dir/glossary.adoc
        unset term
        unset searchterm
        for term in $@
        do
            searchterm="id=\"$term\""
            gawk -v foundterm=$term -v searchterm=$searchterm 'BEGIN {RS="\n\n\n";FS=""} match($0, /\[id="(.+)",.+\]/, m)  { if ( index($0,searchterm) >0 ) { print $0 } }' $glossfileTmp >> $dir/glossary.adoc
            echo -e "\n\n" >> $dir/glossary.adoc
            unset searchterm
        done

        echo -e "\n\n:imagesdir: images\n" >> $dir/glossary.adoc
        echo -e "\n\n" >> $dir/glossary.adoc
    else
           touch $dir/glossary.adoc
    fi

    rm $glossfileTmp
    unset glossfil
    unset glossfilTmp
}

#modified 10_* rausgenommen fuer die Docs hier
allDocDirCmd() {
    find $ArgOneDir/* -name master.adoc | xargs dirname
}

# (1)

echo "Generating document dependent glossaries...."

curDir=$(pwd)
# "DEBUG: started in " $curDir

allGlossaryTerms=$(readGlossaryTerms)

#for irgt in ${allGlossaryTerms[@]}; do
#  echo " RGT : " $irgt
#done

# wechsele in das übergebene Verzeichnis
cd $1
ArgOneDir=$(pwd)

# echo "DEBUG: got ArgOne " $ArgOneDir

allDocDirectories=($(eval "allDocDirCmd"))

# wechsele in das übergebene Arbeitsverzeichnis
cd $curDir

# (2)
for dir in ${allDocDirectories[@]}
do
    echo " Ziel " $dir
    unset foundTerms
    foundTerms=$(findTerms ${allGlossaryTerms[@]})
    #for igt in ${foundTerms[@]}; do
    #  echo " Verweis initial : " $igt
    #done
    buildDocumentGlossary ${foundTerms[@]}
    Counter=0
    # maximale Tiefe für verschachtelte Verweise ist 9 - (countermax in diesem Falle Countermax=10 setzen)
    # einmal durchgehen ist also 2
    CounterMax=9
    ActTerms=($foundTerms)
    OldLength=${#ActTerms[@]}
    while [ $Counter -lt $CounterMax ]; do
      unset foundTerms
      foundTerms=$(findTermswithGlos ${allGlossaryTerms[@]})
      buildDocumentGlossary ${foundTerms[@]}
      ActTerms=($foundTerms)
      NewLength=${#ActTerms[@]}

      #echo -e $Counter
      echo "alt>"  $OldLength " neu> " $NewLength " ( Lauf: " $Counter ")"
      #for ifrt in ${ActTerms[@]}; do
      #  echo " Verweis         : " $ifrt
      #done

      if [ $OldLength -eq $NewLength ]
      then
        Counter=$CounterMax
      else
        OldLength=$NewLength
      fi
      let Counter=Counter+1
    done
done
