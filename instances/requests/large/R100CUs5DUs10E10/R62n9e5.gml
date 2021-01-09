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
    prc 5
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
    type 2
    prc 2
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 2
    prb 4
    x 28
    y 31
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 7
    prb 3
    x 90
    y 99
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 7
    prb 4
    x 114
    y 25
  ]
  node [
    id 7
    label "7"
    type 1
    prc 2
    ant 3
    prb 3
    x 29
    y 18
  ]
  node [
    id 8
    label "8"
    type 1
    prc 5
    ant 5
    prb 3
    x 29
    y 103
  ]
  edge [
    source 0
    target 8
    bandwith 452
    delay 436
  ]
  edge [
    source 1
    target 4
    bandwith 408
    delay 130
  ]
  edge [
    source 2
    target 6
    bandwith 126
    delay 491
  ]
  edge [
    source 3
    target 5
    bandwith 391
    delay 499
  ]
  edge [
    source 3
    target 7
    bandwith 980
    delay 272
  ]
]
