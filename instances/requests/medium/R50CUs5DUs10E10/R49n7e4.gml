graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
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
    prc 5
    ant 7
    prb 4
    x 58
    y 13
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 4
    prb 5
    x 22
    y 19
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 7
    prb 6
    x 22
    y 46
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 7
    prb 3
    x 41
    y 44
  ]
  edge [
    source 0
    target 4
    bandwith 561
    delay 465
  ]
  edge [
    source 1
    target 3
    bandwith 323
    delay 211
  ]
  edge [
    source 2
    target 6
    bandwith 831
    delay 418
  ]
  edge [
    source 2
    target 5
    bandwith 445
    delay 115
  ]
]
