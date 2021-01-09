graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 4
    prb 3
    x 34
    y 37
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 3
    prb 4
    x 39
    y 99
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 9
    prb 4
    x 62
    y 29
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 9
    prb 6
    x 87
    y 117
  ]
  edge [
    source 0
    target 4
    bandwith 614
    delay 393
  ]
  edge [
    source 1
    target 6
    bandwith 425
    delay 372
  ]
  edge [
    source 2
    target 3
    bandwith 706
    delay 480
  ]
  edge [
    source 2
    target 5
    bandwith 348
    delay 287
  ]
]
