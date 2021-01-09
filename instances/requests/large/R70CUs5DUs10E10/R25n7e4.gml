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
    prc 2
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
    prc 4
    ant 7
    prb 3
    x 63
    y 33
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 10
    prb 5
    x 20
    y 82
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 8
    prb 3
    x 75
    y 104
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 8
    prb 6
    x 20
    y 61
  ]
  edge [
    source 0
    target 6
    bandwith 306
    delay 370
  ]
  edge [
    source 1
    target 3
    bandwith 963
    delay 397
  ]
  edge [
    source 2
    target 4
    bandwith 689
    delay 331
  ]
  edge [
    source 2
    target 5
    bandwith 138
    delay 128
  ]
]
