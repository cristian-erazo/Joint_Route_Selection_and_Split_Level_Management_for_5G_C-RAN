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
    type 1
    prc 2
    ant 9
    prb 2
    x 100
    y 108
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 9
    prb 4
    x 46
    y 34
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 5
    prb 2
    x 29
    y 102
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 10
    prb 3
    x 68
    y 71
  ]
  edge [
    source 0
    target 2
    bandwith 676
    delay 281
  ]
  edge [
    source 0
    target 3
    bandwith 880
    delay 186
  ]
  edge [
    source 1
    target 5
    bandwith 670
    delay 378
  ]
  edge [
    source 1
    target 4
    bandwith 354
    delay 304
  ]
]
