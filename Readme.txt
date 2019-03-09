===============================================================================
TEMA 2 - APD
Matei Bianca-Larisa -> 332CB
===============================================================================


-------------------------------------------------------------------------------
    CommunicationChannel.java
-------------------------------------------------------------------------------
    Pentru a implementa aceasta clasa, am folosit doua LinkedBlockingQueue
pentru a retine mesajele minerilor, respectiv pe ale vrajitorilor. In cazul
minerilor, nu mai este nevoie de altceva in afara de a folosi metodele put si
take (dupa modelul multipleProducersMultipleConsumers folosing BlockingQueue).
Pentru wizardChannel, am folosit cate un ReentrantLock pentru a sincroniza cele
doua mesaje trimise de vrajitori (care contin parentRoom si currentRoom). In 
putMessageWizardChannel ignor mesajele de tip "END" si "EXIT" (nu influenteaza 
cu nimic), pun lock(), adaug mesajul in coada, iar daca s-au pus cele doua 
mesaje (firstLock.getHoldCount == 2), dau unlock() de doua ori. Dupa aceea, am 
facut acelasi lucru si in getMessageWizardChannel. 
*******************************************************************************


-------------------------------------------------------------------------------
    Miner.java
-------------------------------------------------------------------------------
    In metoda Run a clasei Miner am creat o bucla infinita in care iau cele doua 
mesaje de la vrajitori folosind metoda getMessageWizardChannel() (acestea sunt 
sincronizate in CommunicationChannel) si verific daca currentRoom a fost deja 
vizitata (se afla in solved). In cazul in care nu a fost vizitata, hash-uiesc 
stringul primit in al doilea mesaj de la vrajitor folosind metoda 
encryptMultipleTimes(), salvez camera curenta din al doilea mesaj, respectiv 
camera parinte din primul mesaj, adaug camera curenta in setul solved si creez 
mesajul pentru vrajitori.
*******************************************************************************